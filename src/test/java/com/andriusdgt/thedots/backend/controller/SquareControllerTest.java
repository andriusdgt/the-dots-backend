package com.andriusdgt.thedots.backend.controller;

import com.andriusdgt.thedots.core.model.Point;
import com.andriusdgt.thedots.core.model.Square;
import com.andriusdgt.thedots.core.service.SquareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class SquareControllerTest {

    @Mock
    private SquareService squareService;

    private SquareController squareController;

    @BeforeEach
    public void setUp() {
        squareController = new SquareController(squareService);
    }

    @Test
    public void findsSquares() {
        List<Square> squares =
            Collections.singletonList(new Square(new Point(10, -10, "listId"), new Point(10, 10, "listId")));
        doReturn(squares).when(squareService).find("listId");

        assertEquals(squares, squareController.find("listId"));
    }

}
