package com.andriusdgt.thedots.backend.controller;

import com.andriusdgt.thedots.core.model.*;
import com.andriusdgt.thedots.core.repository.PointListRepository;
import com.andriusdgt.thedots.core.repository.PointRepository;
import com.andriusdgt.thedots.core.service.PointListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
final class PointListControllerTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointListRepository pointListRepository;

    @Mock
    private PointListService pointListService;

    private PointListController pointListController;

    private static final long POINT_LIST_SIZE_LIMIT = 100;

    @BeforeEach
    void setUp() {
        pointListController =
            new PointListController(POINT_LIST_SIZE_LIMIT, pointRepository, pointListRepository, pointListService);
    }

    @Test
    @SuppressWarnings("unchecked")
    void createsPointsFromFile() throws IOException {
        String fileContent = "10 -10\n-20 20\nsome text";
        MockMultipartFile file = new MockMultipartFile("file", "points.txt", "text/plain", fileContent.getBytes());
        HashSet<Object> expectedWarnings = new HashSet<>();
        ArgumentCaptor<Stream<String>> streamCaptor = ArgumentCaptor.forClass(Stream.class);
        doReturn(expectedWarnings).when(pointListService).create(any(), eq("listId"), eq(POINT_LIST_SIZE_LIMIT));

        Set<Warning> actualWarnings = pointListController.create(file, "listId");

        verify(pointListService).create(streamCaptor.capture(), eq("listId"), eq(POINT_LIST_SIZE_LIMIT));
        assertEquals(expectedWarnings, actualWarnings);
        assertEquals(
            fileContent.lines().collect(Collectors.toList()),
            streamCaptor.getValue().collect(Collectors.toList())
        );
    }

    @Test
    void createsPointList() {
        PointList pointList = new PointList("listId", "name");

        pointListController.create(pointList);

        verify(pointListService).create(pointList);
    }

    @Test
    void getsPointList() {
        List<PointList> pointLists = Collections.singletonList(new PointList("listId", "name"));
        doReturn(pointLists).when(pointListRepository).findAll();

        assertEquals(pointLists, pointListController.get());
    }

    @Test
    void downloadsPointListTextFile() throws IOException {
        String points = "10 -10\n-20 30";
        doReturn(points).when(pointListService).getPoints("listId");

        ResponseEntity<byte[]> responseEntity = pointListController.downloadPointListTextFile("listId");

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals(MediaType.TEXT_PLAIN, responseEntity.getHeaders().getContentType());
        assertEquals(
            "attachment; filename=\"point list listId.txt\"",
            responseEntity.getHeaders().getContentDisposition().toString()
        );
        assertArrayEquals(points.getBytes(UTF_8), responseEntity.getBody());
    }

    @Test
    void deletesPointList() {
        pointListController.delete("listId");

        verify(pointListRepository).deleteById("listId");
        verify(pointRepository).deleteByListId("listId");
    }

}
