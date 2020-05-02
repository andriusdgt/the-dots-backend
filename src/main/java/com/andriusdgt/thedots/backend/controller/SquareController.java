package com.andriusdgt.thedots.backend.controller;

import com.andriusdgt.thedots.core.model.Square;
import com.andriusdgt.thedots.core.service.SquareService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point/list/square")
final class SquareController {

    private final SquareService squareService;

    public SquareController(SquareService squareService) {
        this.squareService = squareService;
    }

    @GetMapping("/list-id/{listId}")
    public List<Square> find(@PathVariable String listId) {
        return squareService.find(listId);
    }

}
