package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface empRepo extends MongoRepository<Employee, String> {
    public Employee findByEmpCodeAndEmpName(String empCode, String empName);
    public List<Employee> findByEmpRole(String empRole);
    
}
