package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.repositories.foodRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class foodService {
    @Autowired
    foodRepo foodRepo;

    public Food add(Food food) {
        return foodRepo.save(food);
    }

    public List<Food> getAll() {
        return foodRepo.findAll();
    }

    public Food findByCode(String code) {
        Food food = foodRepo.findById(code).get();
        return food;
    }

    public void delete(String code) {
        foodRepo.deleteById(code);
    }

}
