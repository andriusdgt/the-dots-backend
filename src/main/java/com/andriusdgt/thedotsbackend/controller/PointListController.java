package com.andriusdgt.thedotsbackend.controller;

import com.andriusdgt.thedotsbackend.model.PointCoordinates;
import com.andriusdgt.thedotsbackend.repository.PointCoordinatesRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Validator;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/point/list")
public class PointListController {

    long pointCoordinatesListSize;
    Validator validator;
    PointCoordinatesRepository pointCoordinatesRepository;

    public PointListController(
            @Value("${POINT_COORDINATES_LIST_SIZE}") long pointCoordinatesListSize,
            Validator validator,
            PointCoordinatesRepository pointCoordinatesRepository
    ) {
        this.pointCoordinatesListSize = pointCoordinatesListSize;
        this.validator = validator;
        this.pointCoordinatesRepository = pointCoordinatesRepository;
    }

    @PostMapping("/list-id/{listId}")
    public Set<String> add(@PathVariable String listId, @RequestParam("file") MultipartFile pointsFile) throws IOException {
        Set<String> errors = new HashSet<>();
        List<PointCoordinates> pointList = IOUtils
                .readLines(pointsFile.getInputStream(), StandardCharsets.UTF_8)
                .stream()
                .peek(line -> {
                    if (!line.matches("[-]?\\d+ [-]?\\d+"))
                        errors.add("Found incorrectly formatted lines, ignoring");
                })
                .filter(line -> line.matches("[-]?\\d+ [-]?\\d+"))
                .map(line -> new AbstractMap.SimpleEntry<>(line.split(" ")[0], line.split(" ")[1]))
                .map(pair -> new PointCoordinates(Short.parseShort(pair.getKey()), Short.parseShort(pair.getValue()), listId))
                .collect(Collectors.toList());

        int pointCount = pointList.size();

        pointList = pointList.stream().distinct().collect(Collectors.toList());
        List<PointCoordinates> existingPoints = pointCoordinatesRepository.findByListId(listId);
        pointList.removeAll(existingPoints);
        if (pointList.size() != pointCount)
            errors.add("Found duplicates, only distinct ones will be preserved");

        if (pointList.size() + existingPoints.size() > pointCoordinatesListSize) {
            errors.add("New points exceeds list size limit of " + pointCoordinatesListSize + ", not all points will be imported");
            pointList = pointList.subList(0, (int) pointCoordinatesListSize - existingPoints.size());
        }

        pointCoordinatesRepository.saveAll(pointList);
        return errors;
    }

}
