package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.Food;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface foodRepo extends MongoRepository<Food,String> {
    Food findTopByOrderByFoodCodeDesc();

   @Query("{$or: [{'foodCode': {$regex: ?0, $options: 'i'}}, {'foodName': {$regex: ?0, $options: 'i'}}]}")
   List<Food> findByFoodCodeOrName(String foodCode);
}
