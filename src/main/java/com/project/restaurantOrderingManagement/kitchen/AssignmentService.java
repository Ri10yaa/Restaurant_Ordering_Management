package com.project.restaurantOrderingManagement.kitchen;

import com.project.restaurantOrderingManagement.models.Chef;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import com.project.restaurantOrderingManagement.service.ChefOrderService;
import com.project.restaurantOrderingManagement.waiter.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;

@Service
public class AssignmentService {

    private final ChefOrderService chefOrderService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final empRepo employeeRepository;

    @Autowired
    public AssignmentService(ChefOrderService chefOrderService, RedisTemplate<String, Object> redisTemplate, empRepo employeeRepository) {
        this.chefOrderService = chefOrderService;
        this.redisTemplate = redisTemplate;
        this.employeeRepository = employeeRepository;
    }

    public String assign(String orderKey) {

        String[] parts = orderKey.split(":");
        String foodCode=parts[3];
        String quantity = (String) redisTemplate.opsForHash().get(orderKey, "quantity");
        String status = (String) redisTemplate.opsForHash().get(orderKey, "status");
        if(!status.equals("deleted")) {
            String cuisineSpec = getCuisineSpecFromFoodCode(foodCode);
            List<Chef> availableChefs = employeeRepository.findChefs();

            Chef assignedChef = null;
            int minOrders = Integer.MAX_VALUE;
            for (Chef chef : availableChefs) {
                if (chef.getSpec().contains(cuisineSpec)) {
                    String chefEmpCode = chef.getEmpCode();
                    List<Order> orders = chefOrderService.getOrdersForChef(chefEmpCode);
                    int currentOrderCount = orders.size();
                    if (currentOrderCount < minOrders) {
                        minOrders = currentOrderCount;
                        assignedChef = chef;
                    }
                }
            }

            return assignedChef == null ? "No chef available for this order" : chefOrderService.assignOrderToChef(orderKey, assignedChef.getEmpCode());
        }
        else{
            return "Order already deleted";
        }

    }

    private String getCuisineSpecFromFoodCode(String foodCode) {
        if (foodCode.startsWith("S")) {
            return "South";
        } else if (foodCode.startsWith("N")) {
            return "North";
        } else if (foodCode.startsWith("C")) {
            return "Chinese";
        }
        else{
            return "Other";
        }
    }

}
