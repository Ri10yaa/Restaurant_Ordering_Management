package com.project.restaurantOrderingManagement.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class foodCodeIncrementingService {
    private static final String foodCodeIncrementing = "FoodCodeInc";

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    public foodCodeIncrementingService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String formatCode(long code) {
        return String.format("%03d", code);
    }
    public String getFoodCodeIncrementing() {
        long currentFoodCode = (long) redisTemplate.opsForValue().get(foodCodeIncrementing);
        long value = currentFoodCode != 0 ? currentFoodCode : 0;
        return formatCode(value);
    }
    public String incrementFoodCode() {
        long value = (long) redisTemplate.opsForValue().increment(foodCodeIncrementing, 1);
        return formatCode(value);
    }

}
