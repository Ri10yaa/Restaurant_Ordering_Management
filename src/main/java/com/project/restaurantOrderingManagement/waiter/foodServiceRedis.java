package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.Food;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class foodServiceRedis {
    private final RedisTemplate<String, Object> redisTemplate;

    public foodServiceRedis(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void resetAvailability(Map<Food, Integer> foodItems) {
        foodItems.forEach((item, availability) -> redisTemplate.opsForValue().set("food:" + item, availability));
    }

    public void decrementAvailability(String foodCode, int quantity) {
        Integer availability = (Integer) redisTemplate.opsForValue().get("food:" + foodCode);
        System.out.println("Availability : " + availability);
        if (availability != null && availability >= quantity) {
            redisTemplate.opsForValue().decrement("food:" + foodCode, quantity);
        } else {
            throw new RuntimeException("Insufficient stock for " + foodCode);
        }
    }

    public void incrementAvailability(String foodCode, int quantity) {
            redisTemplate.opsForValue().increment("food:" + foodCode, quantity);
    }

}
