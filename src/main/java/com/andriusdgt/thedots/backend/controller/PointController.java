package com.andriusdgt.thedots.backend.controller;

import com.andriusdgt.thedots.core.model.Point;
import com.andriusdgt.thedots.core.repository.PointRepository;
import com.andriusdgt.thedots.core.service.PointService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

@RestController
@RequestMapping("/point")
final class PointController {

    private final long pointListSizeLimit;
    private final PointRepository pointRepository;
    private final PointService pointService;

    PointController(
        @Value("${POINT_LIST_SIZE_LIMIT}") long pointListSizeLimit,
        PointRepository pointRepository,
        PointService pointService
    ) {
        this.pointListSizeLimit = pointListSizeLimit;
        this.pointRepository = pointRepository;
        this.pointService = pointService;
    }

    @PutMapping
    void create(@RequestBody Point point) {
        pointService.create(point, pointListSizeLimit);
    }

    @GetMapping("/list-id/{listId}/count")
    long getCount(@PathVariable String listId) {
        return pointRepository.countByListId(listId);
    }

    @GetMapping("/list-id/{listId}/page-index/{pageIndex}/page-size/{pageSize}")
    List<Point> findPaginatedBy(@PathVariable Map<String, String> pathVars) {
        return pointRepository.findByListId(
            pathVars.get("listId"),
            parseInt(pathVars.get("pageIndex")),
            parseInt(pathVars.get("pageSize"))
        );
    }

    @GetMapping("/list-id/{listId}/page-index/{pageIndex}/page-size/{pageSize}/{sortDirection:asc|desc}")
    List<Point> findSortedPaginatedBy(@PathVariable Map<String, String> pathVars) {
        return pointRepository.findByListIdOrderByXAndY(
            pathVars.get("listId"),
            parseInt(pathVars.get("pageIndex")),
            parseInt(pathVars.get("pageSize")),
            pathVars.get("sortDirection")
        );
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        pointRepository.deleteById(id);
    }

    @DeleteMapping("/list-id/{listId}")
    void deletePointsFromList(@PathVariable String listId) {
        pointRepository.deleteByListId(listId);
    }

}
