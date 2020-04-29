package com.andriusdgt.thedots.backend.controller;

import com.andriusdgt.thedots.core.model.Point;
import com.andriusdgt.thedots.core.model.PointList;
import com.andriusdgt.thedots.core.repository.PointListRepository;
import com.andriusdgt.thedots.core.repository.PointRepository;
import com.andriusdgt.thedots.core.service.PointListService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/point/list")
final class PointListController {

    private final long pointListSizeLimit;
    private final PointRepository pointRepository;
    private final PointListRepository pointListRepository;
    private final PointListService pointListService;

    public PointListController(
        @Value("${POINT_LIST_SIZE_LIMIT}") long pointListSizeLimit,
        PointRepository pointRepository,
        PointListRepository pointListRepository,
        PointListService pointListService
    ) {
        this.pointListSizeLimit = pointListSizeLimit;
        this.pointRepository = pointRepository;
        this.pointListRepository = pointListRepository;
        this.pointListService = pointListService;
    }

    @PostMapping("/list-id/{listId}")
    public Set<String> add(@PathVariable String listId, @RequestParam("file") MultipartFile pointsFile) throws IOException {
        return pointListService.create(
            IOUtils.readLines(pointsFile.getInputStream(), StandardCharsets.UTF_8).stream(),
            listId,
            pointListSizeLimit
        );
    }

    @PutMapping
    public void createList(@RequestBody PointList pointList) {
        pointListService.create(pointList);
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
                    IOUtils.toInputStream(pointRepository
                        .findByListId(listId)
                        .stream()
                        .map(Point::toString)
                        .collect(Collectors.joining("\n")), StandardCharsets.UTF_8
                    )
                )
            );
    }

    @GetMapping("/list-id/{listId}/squares")
    public List<List<Point>> findSquares(@PathVariable String listId) {
        return pointListService.findSquares(listId);
    }

    @DeleteMapping("/list-id/{listId}")
    public void deleteList(@PathVariable String listId) {
        pointListRepository.deleteById(listId);
        pointRepository.deleteByListId(listId);
    }

}
