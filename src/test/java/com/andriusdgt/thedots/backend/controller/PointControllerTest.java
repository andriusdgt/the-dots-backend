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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
final class PointControllerTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointService pointService;

    private PointController pointController;

    private static final long POINT_LIST_SIZE_LIMIT = 100L;

    @BeforeEach
    void setUp() {
        pointController = new PointController(POINT_LIST_SIZE_LIMIT, pointRepository, pointService);
    }

    @Test
    void createsPoint() {
        Point point = new Point(10, 20, "listId");

        pointController.create(point);

        verify(pointService).create(point, POINT_LIST_SIZE_LIMIT);
    }

    @Test
    void getsPointCount() {
        doReturn(1L).when(pointRepository).countByListId("listId");

        assertEquals(1L, pointController.getCount("listId"));
    }

    @Test
    void getsPointsInPage() {
        int pageIndex = 3;
        int pageSize = 50;
        String listId = "listId";
        List<Point> expectedPoints = Collections.singletonList(new Point(10, -20, listId));
        doReturn(expectedPoints).when(pointRepository).findByListId(listId, pageIndex, pageSize);

        List<Point> actualPoints = pointController.findPaginatedBy(
            Map.of(
                "listId", listId,
                "pageIndex", String.valueOf(pageIndex),
                "pageSize", String.valueOf(pageSize)
            )
        );

        assertEquals(expectedPoints, actualPoints);
    }

    @Test
    void getsPointsInSortedPage() {
        int pageIndex = 3;
        int pageSize = 50;
        String listId = "listId";
        List<Point> expectedPoints = Collections.singletonList(new Point(10, -20, listId));
        doReturn(expectedPoints).when(pointRepository).findByListIdOrderByXAndY(listId, pageIndex, pageSize, "desc");

        List<Point> actualPoints = pointController.findSortedPaginatedBy(
            Map.of(
                "listId", listId,
                "pageIndex", String.valueOf(pageIndex),
                "pageSize", String.valueOf(pageSize),
                "sortDirection", "desc"
            )
        );

        assertEquals(expectedPoints, actualPoints);
    }

    @Test
    void deletesPoint() {
        pointController.delete("pointId");

        verify(pointRepository).deleteById("pointId");
    }

    @Test
    void deletesPointsByList() {
        pointController.deletePointsFromList("listId");

        verify(pointRepository).deleteByListId("listId");
    }

}
