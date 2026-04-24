package com.project.restaurantOrderingManagement.kitchen;

import com.project.restaurantOrderingManagement.models.Chef;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import com.project.restaurantOrderingManagement.service.ChefOrderService;
import com.project.restaurantOrderingManagement.waiter.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AssignmentService {

    private final ChefOrderService chefOrderService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final empRepo employeeRepository;

    public AssignmentService(ChefOrderService chefOrderService,
                             RedisTemplate<String, Object> redisTemplate,
                             empRepo employeeRepository) {
        this.chefOrderService = chefOrderService;
        this.redisTemplate = redisTemplate;
        this.employeeRepository = employeeRepository;
    }

    public String assign(String orderKey) {
        Map<Object, Object> orderMap = redisTemplate.opsForHash().entries(orderKey);
        if (orderMap == null || orderMap.isEmpty()) {
            return "Order missing in Redis";
        }

        String status = String.valueOf(orderMap.get("status"));
        if ("deleted".equalsIgnoreCase(status)) {
            return "Order already deleted";
        }

        String[] parts = orderKey.split(":");
        if (parts.length < 4) {
            return "Invalid order key";
        }

        String foodCode = parts[3];
        String cuisineSpec = getCuisineSpecFromFoodCode(foodCode);
        List<Chef> availableChefs = employeeRepository.findChefs();

        Chef assignedChef = null;
        int minOrders = Integer.MAX_VALUE;

        for (Chef chef : availableChefs) {
            if (chef.getSpec() == null || !chef.getSpec().contains(cuisineSpec)) {
                continue;
            }

            List<Order> orders = chefOrderService.getOrdersForChef(chef.getEmpCode());
            int currentOrderCount = orders.size();

            if (currentOrderCount < minOrders) {
                minOrders = currentOrderCount;
                assignedChef = chef;
            }
        }

        return assignedChef == null
                ? "No chef available for this order"
                : chefOrderService.assignOrderToChef(orderKey, assignedChef.getEmpCode());
    }

    private String getCuisineSpecFromFoodCode(String foodCode) {
        if (foodCode.startsWith("S")) {
            return "South";
        }
        if (foodCode.startsWith("N")) {
            return "North";
        }
        if (foodCode.startsWith("C")) {
            return "Chinese";
        }
        return "Other";
    }
}
