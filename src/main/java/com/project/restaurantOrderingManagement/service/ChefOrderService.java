package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.OrderNotFoundException;
import com.project.restaurantOrderingManagement.waiter.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ChefOrderService {

    private final RedisTemplate<String, Object> redisTemplate;

    public ChefOrderService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String assignOrderToChef(String orderKey, String chefCode) {
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(orderKey))) {
            throw new OrderNotFoundException("Order does not exist: " + orderKey);
        }

        redisTemplate.opsForHash().put(orderKey, "chefCode", chefCode);
        return "Order " + orderKey + " assigned to chef " + chefCode;
    }

    public String getChefForOrder(String billNo, String foodCode) {
        String orderKey = "orders:bill:" + billNo + ":" + foodCode;
        Object chefId = redisTemplate.opsForHash().get(orderKey, "chefCode");
        return chefId != null ? chefId.toString() : "No chef assigned";
    }

    public List<Order> getOrdersForChef(String chefId) {
        Set<String> keys = redisTemplate.keys("orders:bill:*");
        List<Order> assignedOrders = new ArrayList<>();

        if (keys == null) {
            return assignedOrders;
        }

        for (String key : keys) {
            Map<Object, Object> orderMap = redisTemplate.opsForHash().entries(key);
            Object assignedChef = orderMap.get("chefCode");
            if (assignedChef != null && assignedChef.toString().equals(chefId)) {
                String[] parts = key.split(":");
                String billNo = parts.length > 2 ? parts[2] : null;
                String foodCode = key.substring(key.lastIndexOf(':') + 1);
                assignedOrders.add(Order.mapToOrder(orderMap, foodCode, billNo));
            }
        }

        return assignedOrders;
    }
}
