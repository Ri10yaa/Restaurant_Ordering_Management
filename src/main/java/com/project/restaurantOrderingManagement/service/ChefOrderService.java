package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.models.Employee;
import com.project.restaurantOrderingManagement.repositories.empRepo;
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

    public String enqueueOrder(String foodCode, String quantity, String status, String billno) {
        String cuisineSpec = getCuisineSpecFromFoodCode(foodCode);
        List<Employee> chefs = employeeRepository.findByEmpRole("chef");

        Employee assignedChef = null;
        int minOrders = Integer.MAX_VALUE;

        // Find the chef with the least orders assigned
        for (Employee chef : chefs) {
            if (chef.getEmpCode().startsWith("C") && chef.getSpec().equals(cuisineSpec)) {
                Set<Object> orders = redisTemplate.opsForHash().keys("chefOrders:" + chef.getEmpCode());
                int currentOrderCount = orders.size();

                if (currentOrderCount < minOrders) {
                    minOrders = currentOrderCount;
                    assignedChef = chef;
                }
            }
        }

        if (assignedChef != null) {
            // Use billno directly to create a unique key for each order under the same chef
            String chefOrderKey = "chefOrders:" + assignedChef.getEmpCode() + ":" + billno;

            // Check if the order already exists for the given billno
            if (redisTemplate.hasKey(chefOrderKey)) {
                return "Order with BillNo " + billno + " already exists for chef: " + assignedChef.getEmpCode();
            }

            // Store each order as a separate entry using the unique chefOrderKey
            Map<String, String> orderDetails = new HashMap<>();
            orderDetails.put("billno", billno);
            orderDetails.put("foodCode", foodCode);
            orderDetails.put("quantity", quantity);
            orderDetails.put("status", status);

            redisTemplate.opsForHash().putAll(chefOrderKey, orderDetails);

            System.out.println("Order assigned to chef: " + assignedChef.getEmpCode() + ", BillNo: " + billno);
            return "Order assigned to chef: " + assignedChef.getEmpCode() + ", BillNo: " + billno;
        } else {
            return "No suitable chef available.";
        }
    }

    private String getCuisineSpecFromFoodCode(String foodCode) {
        if (foodCode.startsWith("S")) {
            return "South Indian";
        } else if (foodCode.startsWith("N")) {
            return "North Indian";
        } else {
            return "Other";
        }
    }
}
