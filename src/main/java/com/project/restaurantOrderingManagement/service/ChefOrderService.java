package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.models.Chef;
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

//    public String enqueueOrder(String foodCode, String quantity, String status, String billno) {
//        System.out.println("enque ord cos");
//        String cuisineSpec = getCuisineSpecFromFoodCode(foodCode);
//
//        // Fetching only Chef objects directly
//        List<Chef> availableChefs = employeeRepository.findChefs();
//
//        Chef assignedChef = null;
//        int minOrders = Integer.MAX_VALUE;
//
//        // Find the chef with the least orders assigned
//        for (Chef chef : availableChefs) {
//            if (chef.getSpec().contains(cuisineSpec)) { // Use equals for specialization comparison
//                String chefEmpCode = chef.getEmpCode();
//                Set<Object> orders = redisTemplate.opsForHash().keys("chefOrders:" + chefEmpCode);
//                int currentOrderCount = orders.size();
//
//                if (currentOrderCount < minOrders) {
//                    minOrders = currentOrderCount;
//                    assignedChef = chef;
//                    System.out.println("assign:" + assignedChef.getEmpCode());
//                }
//            }
//        }
//
//        if (assignedChef != null) {
//            System.out.println("enque ord cos af");
//            // Use billno directly to create a unique key for each order under the same chef
//            String chefOrderKey = "chefOrders:" + assignedChef.getEmpCode() + ":" + billno;
//
//            // Check if the order already exists for the given billno
//            if (redisTemplate.hasKey(chefOrderKey)) {
//                return "Order with BillNo " + billno + " already exists for chef: " + assignedChef.getEmpCode();
//            }
//
//            // Store each order as a separate entry using the unique chefOrderKey
//            Map<String, String> orderDetails = new HashMap<>();
//            orderDetails.put("billno", billno);
//            orderDetails.put("foodCode", foodCode);
//            orderDetails.put("quantity", quantity);
//            orderDetails.put("status", status);
//
//            redisTemplate.opsForHash().putAll(chefOrderKey, orderDetails);
//
//            System.out.println("Order assigned to chef: " + assignedChef.getEmpCode() + ", BillNo: " + billno);
//            return "Order assigned to chef: " + assignedChef.getEmpCode() + ", BillNo: " + billno;
//        } else {
//            return "No suitable chef available.";
//        }
//    }
public String enqueueOrder(String foodCode, String quantity, String status, String billno) {
    System.out.println("Enqueue order called");
    String cuisineSpec = getCuisineSpecFromFoodCode(foodCode);

    // Fetching only Chef objects directly
    List<Chef> availableChefs = employeeRepository.findChefs();

    Chef assignedChef = null;
    int minOrders = Integer.MAX_VALUE; // Initialize with a high value

    System.out.println("Cuisine Specialization: " + cuisineSpec);
    System.out.println("Available Chefs: " + availableChefs.size());

    // Find the chef with the least orders assigned
    for (Chef chef : availableChefs) {
        System.out.println("Checking chef: " + chef.getEmpCode() + ", Specializations: " + chef.getSpec());
        if (chef.getSpec().contains(cuisineSpec)) { // Check if the list contains the specialization
            String chefEmpCode = chef.getEmpCode();
            Set<Object> orders = redisTemplate.opsForHash().keys("chefOrders:" + chefEmpCode);
            int currentOrderCount = orders.size();

            System.out.println("Chef: " + chefEmpCode + ", Orders: " + currentOrderCount);

            if (currentOrderCount < minOrders) {
                minOrders = currentOrderCount;
                assignedChef = chef;
                System.out.println("New assigned chef: " + assignedChef.getEmpCode());
            }
        }
    }

    if (assignedChef != null) {
        System.out.println("Enqueue order successful");
        // Use billno directly to create a unique key for each order under the same chef
        String chefOrderKey = "chefOrders:" + assignedChef.getEmpCode() + ":" + billno;

        // Check if the order already exists for the given billno
        if (redisTemplate.hasKey(chefOrderKey)) {
            System.out.println("Order with BillNo " + billno + " already exists for chef: " + assignedChef.getEmpCode());
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
        System.out.println("No suitable chef available.");
        return "No suitable chef available.";
    }
}

    private String getCuisineSpecFromFoodCode(String foodCode) {
        if (foodCode.startsWith("S")) {
            return "South";
        } else if (foodCode.startsWith("N")) {
            return "North";
        } else {
            return "Other";
        }
    }
}
