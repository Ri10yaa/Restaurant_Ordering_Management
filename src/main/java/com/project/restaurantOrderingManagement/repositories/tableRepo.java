package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.table;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface tableRepo  extends MongoRepository<table,Integer> {
  //  List<table> findAllByWaiterCode(String waiterCode);

}
