package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.repositories.foodRepo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.util.List;


@Service
public class foodAvailabilityService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Integer dinnerQty = 60;
    private static final Integer lunchQty = 75;
    private static final Integer breakfastQty = 50;
    private static final Integer snacksQty = 40;
    private final foodRepo foodRepo;
    private static final String key = "foodAvailability";

    public foodAvailabilityService(RedisTemplate<String, Object> redisTemplate, foodRepo foodRepo) {
        this.redisTemplate = redisTemplate;
        this.foodRepo = foodRepo;
    }

    public void resetAvailability() {
        try{
            List<Food> foods = foodRepo.findAll();
            int quantity;
            for (Food food : foods) {
                quantity = 0;
                if(food.getMealType().contains("snacks")) {
                    quantity = snacksQty;
                }
                if(food.getMealType().contains("dinner")) {
                    quantity = dinnerQty;
                }
                if(food.getMealType().contains("lunch")) {
                    quantity = lunchQty;
                }
                if(food.getMealType().contains("breakfast")) {
                    quantity = breakfastQty;
                }
                System.out.println("Food code : " + food.getFoodCode() + "\nQuantity : " + quantity);
                redisTemplate.opsForHash().put(key, food.getFoodCode(), String.valueOf(quantity));
            }
            System.out.println("Reset availability is complete.");
        }
       catch (Exception e){
            throw new RuntimeException("Error resetting availability" + e.getMessage());
       }

    }

    public String updateAvailability(String foodCode, int quantity) {
        try {
            if (Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, foodCode))) {
                redisTemplate.opsForHash().put(key, foodCode, quantity);
                System.out.println("Updated availability for " + foodCode + " to " + quantity);
                return "Updated availability";
            }
            return "Food code not found";
        } catch (Exception e) {
            throw new RuntimeException("Error updating availability: " + e.getMessage());
        }
    }

    public Integer getAvailability(String foodCode) {
        try {
            Integer availability = (Integer) redisTemplate.opsForHash().get(key, foodCode);
            return availability != null ? availability : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving availability: " + e.getMessage());
        }
    }

    public void decrementAvailability(String foodCode, int quantity) throws IOException {
        Integer availability = (Integer) redisTemplate.opsForHash().get(key, foodCode);
        if (availability != null && availability >= quantity) {
            redisTemplate.opsForHash().put(key, foodCode, availability - quantity);
        } else {
            throw new IOException("Insufficient stock for " + foodCode);
        }
    }

    public void incrementAvailability(String foodCode, int quantity) {
        Integer availability = (Integer) redisTemplate.opsForHash().get(key, foodCode);
        if (availability != null) {
            redisTemplate.opsForHash().put(key,foodCode, availability + quantity);
        }
    }

}
