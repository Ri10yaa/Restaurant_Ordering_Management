package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.FoodNotFoundException;
import com.project.restaurantOrderingManagement.exceptions.OrderNotFoundException;
import com.project.restaurantOrderingManagement.helpers.foodCodeIncrementingService;
import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.models.foodWithAvailability;
import com.project.restaurantOrderingManagement.repositories.foodRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class foodService {
    @Autowired
    foodRepo foodRepo;
    @Autowired
    private final foodCodeIncrementingService foodCodeIncrementing;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
        Food lastfood = foodRepo.findTopByOrderByFoodCodeDesc();
        Integer num = Integer.parseInt(lastfood.getFoodCode().substring(1));
        num++;
        return code+=num;

    }

    public Food addFoodItem(Food food) {
        food.setFoodCode(formCode(food));
        return foodRepo.save(food);
    }

    public List<Food> getAllFoodItems() {
        return foodRepo.findAll();
    }

    public Optional<Food> getFoodItem(String code) {
        return foodRepo.findById(code);
    }

    public void deleteFoodItem(String code) {
        if(foodRepo.existsById(code)) {
            foodRepo.deleteById(code);
        }
        else{
            throw new FoodNotFoundException("Food item with code "+code+" does not exist");
        }
    }

    public Food updateFoodItem(String code,Food food) {
        return foodRepo.findById(code).map(item -> {
            item.setFoodName(food.getFoodName());
            item.setPrice(food.getPrice());
            item.setCategory(food.getCategory());
            item.setMealType(food.getMealType());
            item.setVegetarian(food.isVeg());
            return foodRepo.save(item);
        }).orElseThrow(()-> new FoodNotFoundException("Food item with code "+code+" does not exist"));

    }
    public List<foodWithAvailability> searchFoodByCodeOrName(String keyword) {
        List<Food> searchResults = foodRepo.findByFoodCodeOrName(keyword);

        if (searchResults.isEmpty()) {
            throw new FoodNotFoundException("No food items found for the keyword: " + keyword);
        }

        // Transform Food objects into FoodWithAvailability objects
        List<foodWithAvailability> resultsWithAvailability = new ArrayList<>();
        searchResults.forEach(food -> {
            Object availability = redisTemplate.opsForHash().get("foodAvailability", food.getFoodCode());
            resultsWithAvailability.add(new foodWithAvailability(food, Integer.parseInt(availability.toString())));
        });

        return resultsWithAvailability;
    }
}
