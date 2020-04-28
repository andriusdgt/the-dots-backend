package com.andriusdgt.thedotsbackend.controller;

import com.andriusdgt.thedots.api.model.PointCoordinates;
import com.andriusdgt.thedots.api.model.PointList;
import com.andriusdgt.thedots.mongoplugin.repository.PointCoordinatesRepository;
import com.andriusdgt.thedots.mongoplugin.repository.PointListRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ValidationException;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/point/list")
public class PointListController {

    private long pointCoordinatesListSize;
    private Validator validator;
    private PointCoordinatesRepository pointCoordinatesRepository;
    private PointListRepository pointListRepository;

    public PointListController(
        @Value("${POINT_COORDINATES_LIST_SIZE}") long pointCoordinatesListSize,
        Validator validator,
        PointCoordinatesRepository pointCoordinatesRepository,
        PointListRepository pointListRepository
    ) {
        this.pointCoordinatesListSize = pointCoordinatesListSize;
        this.validator = validator;
        this.pointCoordinatesRepository = pointCoordinatesRepository;
        this.pointListRepository = pointListRepository;
    }

    @PostMapping("/list-id/{listId}")
    public Set<String> add(@PathVariable String listId, @RequestParam("file") MultipartFile pointsFile) throws IOException {
        Set<String> errors = new HashSet<>();
        List<PointCoordinates> points = IOUtils
            .readLines(pointsFile.getInputStream(), StandardCharsets.UTF_8)
            .stream()
            .peek(line -> {
                if (!line.matches("[-]?\\d+ [-]?\\d+"))
                    errors.add("Found incorrectly formatted lines, ignoring");
            })
            .filter(line -> line.matches("[-]?\\d+ [-]?\\d+"))
            .map(line -> new AbstractMap.SimpleEntry<>(line.split(" ")[0], line.split(" ")[1]))
            .map(pair -> new PointCoordinates(Short.parseShort(pair.getKey()), Short.parseShort(pair.getValue()), listId))
            .peek(point -> {
                if (!validator.validate(point).isEmpty())
                    errors.add(validator.validate(point).iterator().next().getMessage());
            })
            .filter(point -> validator.validate(point).isEmpty())
            .collect(Collectors.toList());

        int pointCount = points.size();

        points = points.stream().distinct().collect(Collectors.toList());
        List<PointCoordinates> existingPoints = pointCoordinatesRepository.findByListId(listId);
        points.removeAll(existingPoints);
        if (points.size() != pointCount)
            errors.add("Found duplicates, only distinct ones will be preserved");

        if (points.size() + existingPoints.size() > pointCoordinatesListSize) {
            errors.add("New points exceeds list size limit of " + pointCoordinatesListSize + ", not all points will be imported");
            points = points.subList(0, (int) pointCoordinatesListSize - existingPoints.size());
        }

        pointCoordinatesRepository.saveAll(points);
        return errors;
    }

    @PutMapping
    public void createList(@RequestBody PointList pointList) {
        if (!validator.validate(pointList).isEmpty())
            throw new ValidationException(validator.validate(pointList).iterator().next().getMessage());

        PointList pointListWithDuplicateName = pointListRepository.findByName(pointList.getName());
        if (pointListWithDuplicateName != null && !Objects.equals(pointListWithDuplicateName.getId(), pointList.getId()))
            pointCoordinatesRepository.deleteByListId(pointListWithDuplicateName.getId());
        if (pointListWithDuplicateName != null)
            pointListRepository.delete(pointListWithDuplicateName);
        if (pointList.getId() != null)
            pointListRepository
                .findById(pointList.getId())
                .ifPresent(list -> pointListRepository.delete(list));
        pointListRepository.save(pointList);
    }

    @GetMapping
    public List<PointList> getLists() {
        return pointListRepository.findAll();
    }

    @GetMapping("/list-id/{listId}")
    public ResponseEntity<byte[]> downloadPointListTxt(@PathVariable String listId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"point list %s.txt\"", listId));

        return ResponseEntity
            .ok()
            .headers(headers)
            .body(
                IOUtils.toByteArray(
                    IOUtils.toInputStream(pointCoordinatesRepository
                        .findByListId(listId)
                        .stream()
                        .map(PointCoordinates::toString)
                        .collect(Collectors.joining("\n")), StandardCharsets.UTF_8
                    )
                )
            );
    }

    @GetMapping("/list-id/{listId}/squares")
    public List<List<PointCoordinates>> findSquares(@PathVariable String listId) {
        List<List<PointCoordinates>> squares = new ArrayList<>();
        SortedSet<PointCoordinates> points = new TreeSet<>(pointCoordinatesRepository.findByListId(listId));
        Map<Short, List<PointCoordinates>> xAxisPoints =
            points.stream().collect(Collectors.groupingBy(PointCoordinates::getX, toList()));

        xAxisPoints.values().stream().forEach(pointGroup -> {
            for (int firstPointIndex = 0; firstPointIndex < pointGroup.size() - 1; firstPointIndex++) {
                for (int secondPointIndex = firstPointIndex + 1; secondPointIndex < pointGroup.size(); secondPointIndex++) {
                    short x = pointGroup.get(firstPointIndex).getX();
                    short sideLength = (short) Math.abs(pointGroup.get(secondPointIndex).getY() - pointGroup.get(firstPointIndex).getY());
                    PointCoordinates firstVertex = pointGroup.get(firstPointIndex);
                    PointCoordinates secondVertex = pointGroup.get(secondPointIndex);
                    PointCoordinates thirdVertex = new PointCoordinates((short) (x + sideLength), firstVertex.getY(), listId);
                    PointCoordinates fourthVertex = new PointCoordinates((short) (x + sideLength), secondVertex.getY(), listId);
                    if (xAxisPoints.containsKey((short) (x + sideLength))
                        && xAxisPoints.get((short) (x + sideLength)).containsAll(new HashSet<>(Set.of(thirdVertex, fourthVertex))))
                        squares.add(Arrays.asList(firstVertex, secondVertex, thirdVertex, fourthVertex));
                }
            }
        });

        return squares;
    }

    @DeleteMapping("/list-id/{listId}")
    public void deleteList(@PathVariable String listId) {
        pointListRepository.deleteById(listId);
        pointCoordinatesRepository.deleteByListId(listId);
    }

    @DeleteMapping
    public void deleteAll() {
        pointListRepository.deleteAll();
    }

}
