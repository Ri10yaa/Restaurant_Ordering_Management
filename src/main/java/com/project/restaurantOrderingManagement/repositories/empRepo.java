package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface empRepo extends MongoRepository<Employee,String> {
    Employee findByempCodeandempName(String empCode, String empName);
}
