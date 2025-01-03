package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.Log;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface logRepo extends MongoRepository<Log, Object> {
    List<Log> findAllByWaiterCode(String code);

    List<Log> findAllByDate(Date date);
}
