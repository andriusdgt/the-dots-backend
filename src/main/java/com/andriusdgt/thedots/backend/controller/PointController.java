package com.andriusdgt.thedots.backend.controller;

import com.andriusdgt.thedots.core.model.Point;
import com.andriusdgt.thedots.core.repository.PointRepository;
import com.andriusdgt.thedots.core.service.PointService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
final class PointController {

    private final long pointListSizeLimit;
    private final PointRepository pointRepository;
    private final PointService pointService;

    public PointController(
        @Value("${POINT_LIST_SIZE_LIMIT}") long pointListSizeLimit,
        PointRepository pointRepository,
        PointService pointService
    ) {
        this.pointListSizeLimit = pointListSizeLimit;
        this.pointRepository = pointRepository;
        this.pointService = pointService;
    }

    @PutMapping
    public void create(@RequestBody Point point) {
        pointService.create(point, pointListSizeLimit);
    }

    @GetMapping("/list-id/{listId}/count")
    public long getCount(@PathVariable String listId) {
        return pointRepository.countByListId(listId);
    }

    @GetMapping("/list-id/{listId}/page-index/{pageIndex}/page-size/{pageSize}")
    public List<Point> findBy(@PathVariable String listId, @PathVariable int pageIndex, @PathVariable int pageSize) {
        return pointRepository.findByListId(listId, pageIndex, pageSize);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        pointRepository.deleteById(id);
    }

    @DeleteMapping("/list-id/{listId}")
    public void deletePointsFromList(@PathVariable String listId) {
        pointRepository.deleteByListId(listId);
    }

}
