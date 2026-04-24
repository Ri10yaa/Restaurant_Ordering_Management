package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.table;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface tableRepo extends MongoRepository<table, Integer> {
    table findTopByOrderByTableNoDesc();
}
