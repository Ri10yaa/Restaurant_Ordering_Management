package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.table;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface tableRepo  extends MongoRepository<table,Integer> {
    List<table> findAllByWaiterCode(String waiterCode);
    List<table> findAllByStatus(String status);

}
