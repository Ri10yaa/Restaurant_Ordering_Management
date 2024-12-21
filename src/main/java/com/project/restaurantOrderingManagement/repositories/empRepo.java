package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface empRepo extends MongoRepository<Employee,String> {
}
