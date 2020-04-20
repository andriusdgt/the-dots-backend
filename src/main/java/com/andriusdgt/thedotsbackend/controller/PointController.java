package com.andriusdgt.thedotsbackend.controller;

import com.andriusdgt.thedotsbackend.model.PointCoordinates;
import com.andriusdgt.thedotsbackend.repository.PointCoordinatesRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PointController {

    PointCoordinatesRepository pointCoordinatesRepository;

    public PointController(PointCoordinatesRepository pointCoordinatesRepository) {
        this.pointCoordinatesRepository = pointCoordinatesRepository;
    }

    @PostMapping("/add-point")
    public void add(@RequestBody PointCoordinates point) {
        pointCoordinatesRepository.save(point);
    }

    @GetMapping("/get-points")
    public List<PointCoordinates> findAll() {
        return pointCoordinatesRepository.findAll();
    }

}
