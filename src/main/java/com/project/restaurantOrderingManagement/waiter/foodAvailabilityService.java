package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.repositories.foodRepo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class foodAvailabilityService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Integer dinnerQty = 60;
    private static final Integer lunchQty = 75;
    private static final Integer breakfastQty = 50;
    private static final Integer snacksQty = 40;
    private final foodRepo foodRepo;
    private static final String key = "food:";

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
                redisTemplate.opsForValue().set(key + food.getFoodCode(),quantity);
            }
            System.out.println("Reset availability is complete.");
        }
       catch (Exception e){
            throw new RuntimeException("Error resetting availability" + e.getMessage());
       }

    }

    public String updateAvailability(String foodCode, int quantity) {
        try {
            Integer oldQuantity = (Integer) redisTemplate.opsForValue().get(key + foodCode);
            if(oldQuantity != quantity) {
                redisTemplate.opsForValue().set(key + foodCode, quantity);
            }
            else{
                return "Already in same availability quantity";
            }
        }catch (Exception e){
            throw new RuntimeException("Error updating availability" + e.getMessage());
        }
        return "Updated availability";
    }

    public Integer getAvailability(String foodCode) {
        Integer availability = (Integer) redisTemplate.opsForValue().get(key + foodCode);
        if (availability == null) {
            return 0;
        }
        return availability;
    }

    public void decrementAvailability(String foodCode, int quantity) throws IOException {
        Integer availability = (Integer) redisTemplate.opsForValue().get(key + foodCode);
        System.out.println("Availability : " + availability);
        if (availability != null && availability >= quantity) {
            redisTemplate.opsForValue().decrement(key + foodCode, quantity);
        } else {
            System.out.println("Not availabilty");
            throw new IOException("Insufficient stock for " + foodCode);
        }
    }

    public void incrementAvailability(String foodCode, int quantity) {
            redisTemplate.opsForValue().increment(key + foodCode, quantity);
    }

}
