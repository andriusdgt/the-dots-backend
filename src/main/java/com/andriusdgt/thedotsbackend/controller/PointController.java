package com.andriusdgt.thedotsbackend.controller;

import com.andriusdgt.thedots.api.model.PointCoordinates;
import com.andriusdgt.thedots.core.exception.DuplicatePointException;
import com.andriusdgt.thedots.core.exception.TooManyPointsException;
import com.andriusdgt.thedots.mongoplugin.repository.PointCoordinatesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private long pointCoordinatesListSize;
    private Validator validator;
    private PointCoordinatesRepository pointCoordinatesRepository;

    public PointController(
        @Value("${POINT_COORDINATES_LIST_SIZE}") long pointCoordinatesListSize,
        Validator validator,
        PointCoordinatesRepository pointCoordinatesRepository
    ) {
        this.pointCoordinatesListSize = pointCoordinatesListSize;
        this.validator = validator;
        this.pointCoordinatesRepository = pointCoordinatesRepository;
    }

    @PutMapping
    public void add(@RequestBody PointCoordinates point) {
        if (!validator.validate(point).isEmpty())
            throw new ValidationException(validator.validate(point).iterator().next().getMessage());

        if (pointCoordinatesRepository.exists(Example.of(point)))
            throw new DuplicatePointException();

        long pointCount = pointCoordinatesRepository.countByListId(point.getListId());
        if (pointCount + 1 > pointCoordinatesListSize)
            throw new TooManyPointsException(pointCount);

        pointCoordinatesRepository.save(point);
    }

    @GetMapping
    public List<PointCoordinates> findAll() {
        return pointCoordinatesRepository.findAll();
    }

    @GetMapping("/list-id/{listId}/count")
    public long getPointCount(@PathVariable String listId) {
        return pointCoordinatesRepository.countByListId(listId);
    }

    @GetMapping("/list-id/{listId}/page-index/{pageIndex}/page-size/{pageSize}")
    public List<PointCoordinates> findBy(
        @PathVariable String listId, @PathVariable int pageIndex, @PathVariable int pageSize
    ) {
        return pointCoordinatesRepository.findByListId(listId, PageRequest.of(pageIndex, pageSize));
    }

    @DeleteMapping
    public void deleteAll() {
        pointCoordinatesRepository.deleteAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        pointCoordinatesRepository.deleteById(id);
    }

    @DeleteMapping("/list-id/{listId}")
    public void deletePointsFromList(@PathVariable String listId) {
        pointCoordinatesRepository.deleteByListId(listId);
    }

}
