package com.andriusdgt.thedotsbackend.controller;

import com.andriusdgt.thedotsbackend.exception.DuplicatePointException;
import com.andriusdgt.thedotsbackend.exception.TooManyPointsException;
import com.andriusdgt.thedotsbackend.model.PointCoordinates;
import com.andriusdgt.thedotsbackend.repository.PointCoordinatesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    long pointCoordinatesListSize;
    Validator validator;
    PointCoordinatesRepository pointCoordinatesRepository;

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

        long pointCount = pointCoordinatesRepository.count();
        if (pointCount >= pointCoordinatesListSize)
            throw new TooManyPointsException(pointCount);

        pointCoordinatesRepository.save(point);
    }

    @GetMapping
    public List<PointCoordinates> findAll() {
        return pointCoordinatesRepository.findAll();
    }

    @GetMapping("/list-id/{listId}")
    public List<PointCoordinates> findBy(@PathVariable String listId) {
        return pointCoordinatesRepository.findByListId(listId);
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
    public void deleteBy(@PathVariable String listId) {
        pointCoordinatesRepository.deleteByListId(listId);
    }

}
