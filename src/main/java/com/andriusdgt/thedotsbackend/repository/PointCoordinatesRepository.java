package com.andriusdgt.thedotsbackend.repository;

import com.andriusdgt.thedotsbackend.model.PointCoordinates;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PointCoordinatesRepository extends MongoRepository<PointCoordinates, String> {

    List<PointCoordinates> findAll();
    List<PointCoordinates> findByListId(String listId);
    List<PointCoordinates> findByListId(String listId, Pageable pageable);
    long countByListId(String listId);
    void deleteByListId(String listId);

}
