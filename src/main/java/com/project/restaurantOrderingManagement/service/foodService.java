package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.FoodNotFoundException;
import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.models.foodWithAvailability;
import com.project.restaurantOrderingManagement.repositories.foodRepo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class foodService {

    private final foodRepo foodRepo;
    private final RedisTemplate<String, Object> redisTemplate;

    public foodService(foodRepo foodRepo, RedisTemplate<String, Object> redisTemplate) {
        this.foodRepo = foodRepo;
        this.redisTemplate = redisTemplate;
    }

    private String formCode(Food food) {
        String prefix;
        if (food.getCategory().equalsIgnoreCase("south")) {
            prefix = "S";
        } else if (food.getCategory().equalsIgnoreCase("north")) {
            prefix = "N";
        } else if (food.getCategory().equalsIgnoreCase("chinese")) {
            prefix = "C";
        } else {
            prefix = "O";
        }

        Food lastFood = foodRepo.findTopByOrderByFoodCodeDesc();
        int next = 1;
        if (lastFood != null && lastFood.getFoodCode() != null && lastFood.getFoodCode().length() > 1) {
            next = Integer.parseInt(lastFood.getFoodCode().substring(1)) + 1;
        }
        return prefix + next;
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
        if (!foodRepo.existsById(code)) {
            throw new FoodNotFoundException("Food item with code " + code + " does not exist");
        }
        foodRepo.deleteById(code);
    }

    public Food updateFoodItem(String code, Food food) {
        return foodRepo.findById(code).map(item -> {
            item.setFoodName(food.getFoodName());
            item.setPrice(food.getPrice());
            item.setCategory(food.getCategory());
            item.setMealType(food.getMealType());
            item.setVegetarian(food.isVeg());
            return foodRepo.save(item);
        }).orElseThrow(() -> new FoodNotFoundException("Food item with code " + code + " does not exist"));
    }

    public List<foodWithAvailability> searchFoodByCodeOrName(String keyword) {
        List<Food> searchResults = foodRepo.findByFoodCodeOrName(keyword);
        if (searchResults.isEmpty()) {
            throw new FoodNotFoundException("No food items found for keyword: " + keyword);
        }

        return toFoodAvailability(searchResults);
    }

    public List<foodWithAvailability> getAllFoodWithAvailability() {
        return toFoodAvailability(foodRepo.findAll());
    }

    public String setDailyQuantity(String foodCode, int quantity) {
        Food food = foodRepo.findById(foodCode)
                .orElseThrow(() -> new FoodNotFoundException("Food item with code " + foodCode + " does not exist"));
        redisTemplate.opsForHash().put("foodAvailability", food.getFoodCode(), String.valueOf(quantity));
        return "Daily quantity updated";
    }

    private List<foodWithAvailability> toFoodAvailability(List<Food> foods) {
        List<foodWithAvailability> resultsWithAvailability = new ArrayList<>();
        for (Food food : foods) {
            Object availabilityObj = redisTemplate.opsForHash().get("foodAvailability", food.getFoodCode());
            int availability = availabilityObj != null ? Integer.parseInt(availabilityObj.toString()) : 0;
            resultsWithAvailability.add(new foodWithAvailability(food, availability));
        }
        return resultsWithAvailability;
    }
}
