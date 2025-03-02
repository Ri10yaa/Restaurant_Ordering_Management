package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.models.Chef;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import com.project.restaurantOrderingManagement.waiter.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

@Service
public class ChefOrderService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private empRepo employeeRepository;

    public String assignOrderToChef(String orderKey, String chefCode) {
        if(Boolean.TRUE.equals(redisTemplate.hasKey(orderKey))) {
            redisTemplate.opsForHash().put(orderKey, "chefCode", chefCode);
            return "Order " + orderKey +" assigned to chef: " + chefCode;
        }
        else{
            throw new RuntimeException("Order does not exist");
        }
    }

    public String getChefForOrder(String billNo, String foodCode) {
        String orderKey = "orders:billNo:" + billNo + ":" + foodCode;

        Object chefId = redisTemplate.opsForHash().get(orderKey, "chefCode");

        return chefId != null ? chefId.toString() : "No chef assigned";
    }

    public List<Order> getOrdersForChef(String chefId) {
        Set<String> keys = redisTemplate.keys("orders:bill:*");
        List<Order> assignedOrders = new ArrayList<>();

        if (keys != null) {
            for (String key : keys) {
                Order order = (Order) redisTemplate.opsForHash().entries(key);
                Object assignedChef = redisTemplate.opsForHash().get(key, "chefCode");
                if (assignedChef != null && assignedChef.toString().equals(chefId)) {
                    assignedOrders.add(order);
                }
            }
        }
        return assignedOrders;
    }


}
