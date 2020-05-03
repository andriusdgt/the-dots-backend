package com.andriusdgt.thedots.backend.controller;

import com.andriusdgt.thedots.core.model.*;
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
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RestController
@RequestMapping("/point/list")
final class PointListController {

    private final long pointListSizeLimit;
    private final PointRepository pointRepository;
    private final PointListRepository pointListRepository;
    private final PointListService pointListService;

    PointListController(
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
    Set<Warning> addToList(
        @RequestParam("file") MultipartFile pointsFile, @PathVariable String listId
    ) throws IOException {

        return pointListService.create(
            IOUtils.readLines(pointsFile.getInputStream(), UTF_8).stream(),
            listId,
            pointListSizeLimit
        );

    }

    @PutMapping
    void create(@RequestBody PointList pointList) {
        pointListService.create(pointList);
    }

    @GetMapping
    List<PointList> get() {
        return pointListRepository.findAll();
    }

    @GetMapping("/list-id/{listId}")
    ResponseEntity<byte[]> downloadPointListTextFile(@PathVariable String listId) throws IOException {
        return ResponseEntity
            .ok()
            .headers(createTextFileHeaders(listId))
            .body(IOUtils.toByteArray(IOUtils.toInputStream(pointListService.getPoints(listId), UTF_8)));
    }

    @DeleteMapping("/list-id/{listId}")
    void delete(@PathVariable String listId) {
        pointListRepository.deleteById(listId);
        pointRepository.deleteByListId(listId);
    }

    private HttpHeaders createTextFileHeaders(String listId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        headers.add(CONTENT_DISPOSITION, String.format("attachment; filename=\"point list %s.txt\"", listId));
        return headers;
    }

}
