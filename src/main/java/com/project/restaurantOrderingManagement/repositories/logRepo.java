package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.Log;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface logRepo extends MongoRepository<Log, Integer> {
    List<Log> findAllByWaiterCode(String code);

    List<Log> findAllByDate(Date date);
}
