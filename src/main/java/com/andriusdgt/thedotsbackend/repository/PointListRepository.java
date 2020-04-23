package com.andriusdgt.thedotsbackend.repository;

import com.andriusdgt.thedotsbackend.model.PointList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface PointListRepository extends MongoRepository<PointList, String> {

    PointList findByName(String listId);

}
