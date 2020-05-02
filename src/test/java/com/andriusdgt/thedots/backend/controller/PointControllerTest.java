package com.andriusdgt.thedots.backend.controller;

import com.andriusdgt.thedots.core.model.Point;
import com.andriusdgt.thedots.core.repository.PointRepository;
import com.andriusdgt.thedots.core.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PointControllerTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointService pointService;

    private PointController pointController;

    static final long POINT_LIST_SIZE_LIMIT = 100L;

    @BeforeEach
    public void setUp() {
        pointController = new PointController(POINT_LIST_SIZE_LIMIT, pointRepository, pointService);
    }

    @Test
    public void createsAPoint() {
        Point point = new Point(10, 20, "listId");

        pointController.create(point);

        verify(pointService).create(point, POINT_LIST_SIZE_LIMIT);
    }

    @Test
    public void getsPointCount() {
        doReturn(1L).when(pointRepository).countByListId("listId");

        assertEquals(1L, pointController.getCount("listId"));
    }

    @Test
    public void getsPointsInPage() {
        int pageIndex = 3;
        int pageSize = 50;
        String listId = "listId";
        List<Point> expectedPoints = Collections.singletonList(new Point(10, -20, listId));
        doReturn(expectedPoints).when(pointRepository).findByListId(listId, pageIndex, pageSize);

        List<Point> actualPoints = pointController.findBy(listId, pageIndex, pageSize, null);

        assertEquals(expectedPoints, actualPoints);
    }

    @Test
    public void getsPointsInSortedPage() {
        int pageIndex = 3;
        int pageSize = 50;
        String listId = "listId";
        List<Point> expectedPoints = Collections.singletonList(new Point(10, -20, listId));
        doReturn(expectedPoints).when(pointRepository).findByListIdOrderByXAndY(listId, pageIndex, pageSize, "desc");

        List<Point> actualPoints = pointController.findBy(listId, pageIndex, pageSize, "desc");

        assertEquals(expectedPoints, actualPoints);
    }

    @Test
    public void deletesAPoint() {
        pointController.delete("pointId");

        verify(pointRepository).deleteById("pointId");
    }

    @Test
    public void deletesPointsByList() {
        pointController.deletePointsFromList("listId");

        verify(pointRepository).deleteByListId("listId");
    }

}
