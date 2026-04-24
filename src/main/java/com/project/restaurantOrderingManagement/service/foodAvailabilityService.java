package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.OperationFailedException;
import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.repositories.foodRepo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class foodAvailabilityService {

    private static final int DINNER_QTY = 60;
    private static final int LUNCH_QTY = 75;
    private static final int BREAKFAST_QTY = 50;
    private static final int SNACKS_QTY = 40;
    private static final String KEY = "foodAvailability";

    private final RedisTemplate<String, Object> redisTemplate;
    private final foodRepo foodRepo;

    public foodAvailabilityService(RedisTemplate<String, Object> redisTemplate, foodRepo foodRepo) {
        this.redisTemplate = redisTemplate;
        this.foodRepo = foodRepo;
    }

    public void resetAvailability() {
        List<Food> foods = foodRepo.findAll();
        for (Food food : foods) {
            int quantity = 0;
            if (food.getMealType().contains("snacks")) {
                quantity = SNACKS_QTY;
            }
            if (food.getMealType().contains("dinner")) {
                quantity = DINNER_QTY;
            }
            if (food.getMealType().contains("lunch")) {
                quantity = LUNCH_QTY;
            }
            if (food.getMealType().contains("breakfast")) {
                quantity = BREAKFAST_QTY;
            }
            redisTemplate.opsForHash().put(KEY, food.getFoodCode(), String.valueOf(quantity));
        }
    }

    public String updateAvailability(String foodCode, int quantity) {
        if (Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(KEY, foodCode))) {
            redisTemplate.opsForHash().put(KEY, foodCode, quantity);
            return "Updated availability";
        }
        return "Food code not found";
    }

    public Integer getAvailability(String foodCode) {
        Object availabilityObj = redisTemplate.opsForHash().get(KEY, foodCode);
        return availabilityObj != null ? Integer.parseInt(availabilityObj.toString()) : 0;
    }

    public void decrementAvailability(String foodCode, int quantity) throws IOException {
        Object food = redisTemplate.opsForHash().get(KEY, foodCode);
        int availability = food != null ? Integer.parseInt(food.toString()) : 0;

        if (availability < quantity) {
            throw new IOException("Insufficient stock for " + foodCode);
        }

        redisTemplate.opsForHash().put(KEY, foodCode, String.valueOf(availability - quantity));
    }

    public void incrementAvailability(String foodCode, int quantity) {
        Object availabilityObj = redisTemplate.opsForHash().get(KEY, foodCode);
        int availability = availabilityObj != null ? Integer.parseInt(availabilityObj.toString()) : 0;
        redisTemplate.opsForHash().put(KEY, foodCode, String.valueOf(availability + quantity));
    }
}
