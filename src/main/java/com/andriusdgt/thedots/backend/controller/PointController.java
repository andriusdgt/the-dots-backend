package com.andriusdgt.thedots.backend.controller;

import com.andriusdgt.thedots.core.exception.DuplicatePointException;
import com.andriusdgt.thedots.core.exception.TooManyPointsException;
import com.andriusdgt.thedots.core.model.Point;
import com.andriusdgt.thedots.core.repository.PointRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.List;

@RestController
@RequestMapping("/point")
final class PointController {

    private final long pointListSize;
    private final Validator validator;
    private final PointRepository pointRepository;

    public PointController(
        @Value("${POINT_LIST_SIZE}") long pointListSize,
        Validator validator,
        PointRepository pointRepository
    ) {
        this.pointListSize = pointListSize;
        this.validator = validator;
        this.pointRepository = pointRepository;
    }

    @PutMapping
    public void add(@RequestBody Point point) {
        if (!validator.validate(point).isEmpty())
            throw new ValidationException(validator.validate(point).iterator().next().getMessage());

        if (pointRepository.exists(point))
            throw new DuplicatePointException();

        long pointCount = pointRepository.countByListId(point.getListId());
        if (pointCount + 1 > pointListSize)
            throw new TooManyPointsException(pointCount);

        pointRepository.save(point);
    }

    @GetMapping("/list-id/{listId}/count")
    public long getPointCount(@PathVariable String listId) {
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
