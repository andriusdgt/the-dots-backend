package com.andriusdgt.thedotsbackend.controller;

import com.andriusdgt.thedotsbackend.repository.PointCoordinatesRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Validator;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

    @PostMapping
    public void add(@RequestParam("file") MultipartFile pointsFile) throws IOException {
        System.out.println(IOUtils.toString(pointsFile.getInputStream(), StandardCharsets.UTF_8));
    }

}
