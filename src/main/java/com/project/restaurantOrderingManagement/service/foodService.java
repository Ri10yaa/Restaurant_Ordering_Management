package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.DeleteOperationException;
import com.project.restaurantOrderingManagement.exceptions.EntityNotFoundException;
import com.project.restaurantOrderingManagement.helpers.foodCodeIncrementingService;
import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.repositories.foodRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class foodService {
    @Autowired
    foodRepo foodRepo;
    @Autowired
    private final foodCodeIncrementingService foodCodeIncrementing;

    public foodService(foodCodeIncrementingService foodCodeIncrementing) {
        this.foodCodeIncrementing = foodCodeIncrementing;
    }

    private String formCode(Food food) {
        String code = "";
        if (food.getCategory().equalsIgnoreCase("south")) {
            code+="S";
        }
        else if (food.getCategory().equalsIgnoreCase("north")) {
            code+="N";
        }
        else if (food.getCategory().equalsIgnoreCase("chinese")) {
            code+="C";
        }
        String num = foodCodeIncrementing.incrementFoodCode();
        return code+=num;

    }

    public Food addFoodItem(Food food) {
        food.setFoodCode(formCode(food));
        return foodRepo.save(food);
    }

    public List<Food> getAllFoodItems() {
        return foodRepo.findAll();
    }

    public Food getFoodItem(String code) {
        Food food = foodRepo.findById(code).get();
        return food;
    }

    public void deleteFoodItem(String code) {
        if(foodRepo.existsById(code)) {
            try{
                foodRepo.deleteById(code);
            }
            catch(Exception e) {
                throw new DeleteOperationException("Error while deleting food item with code " +code,e);
            }
        }
        else{
            throw new EntityNotFoundException("Food item with code "+code+" does not exist");
        }
    }

    public Food updateFoodItem(String code,Food food) {
        Optional<Food> existingItem = foodRepo.findById(code);
        if (existingItem.isPresent()) {
            Food item = existingItem.get();
            item.setFoodName(food.getFoodName());
            item.setPrice(food.getPrice());
            item.setCategory(food.getCategory());
            item.setMealType(food.getMealType());
            return foodRepo.save(item);
        }
        throw new EntityNotFoundException("Food item not found with ID: " + code);
    }

}
