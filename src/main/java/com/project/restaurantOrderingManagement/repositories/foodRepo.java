package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.Food;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface foodRepo extends MongoRepository<Food,String> {
}
