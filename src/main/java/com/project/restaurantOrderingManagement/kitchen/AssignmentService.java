package com.project.restaurantOrderingManagement.kitchen;

import com.project.restaurantOrderingManagement.service.ChefOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class AssignmentService {

    private final ChefOrderService chefOrderService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public AssignmentService(ChefOrderService chefOrderService, RedisTemplate<String, Object> redisTemplate) {
        this.chefOrderService = chefOrderService;
        this.redisTemplate = redisTemplate;
    }

    public String assign(String orderKey, String billNo) {

        String[] parts = orderKey.split(":");
        String foodCode=parts[3];
        String quantity = (String) redisTemplate.opsForHash().get(orderKey, "quantity");
        String status = (String) redisTemplate.opsForHash().get(orderKey, "status");
        System.out.println(foodCode + " " + quantity + " " + status);
        if (foodCode == null || quantity == null || status == null) {

            return "Order details not found for orderKey: " + orderKey;
        }

        return chefOrderService.enqueueOrder(foodCode, quantity, status, billNo);
    }

}
