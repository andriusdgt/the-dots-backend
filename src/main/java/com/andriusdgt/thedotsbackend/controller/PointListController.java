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
import java.util.AbstractMap;
import java.util.Set;
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
    public void add(@PathVariable String listId, @RequestParam("file") MultipartFile pointsFile) throws IOException {
        Set<PointCoordinates> pointList = IOUtils
                .readLines(pointsFile.getInputStream(), StandardCharsets.UTF_8)
                .stream()
                .sequential()
                .filter(line -> line.matches("[-]*\\d+ [-]*\\d+"))
                .limit(pointCoordinatesListSize)
                .map(line -> new AbstractMap.SimpleEntry<>(line.split(" ")[0], line.split(" ")[1]))
                .map(pair -> new PointCoordinates(Short.parseShort(pair.getKey()), Short.parseShort(pair.getValue()), listId))
                .collect(Collectors.toSet());

        pointCoordinatesRepository.saveAll(pointList);
    }

}
