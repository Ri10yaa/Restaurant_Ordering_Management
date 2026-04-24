package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.RestaurantSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface restaurantSettingsRepo extends MongoRepository<RestaurantSettings, String> {
}
