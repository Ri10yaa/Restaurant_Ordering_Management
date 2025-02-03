package com.project.restaurantOrderingManagement.kitchen;

import com.project.restaurantOrderingManagement.waiter.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class kitchenService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void removeFoodItem(String chefCode, String billno) throws IOException {
        try {
            String orderKey = "chefOrders:" + chefCode + ":" + billno;
            Map<Object, Object> orderData = redisTemplate.opsForHash().entries(orderKey);
            if (orderData.isEmpty()) {
                throw new IOException("Order not found for chef code " + chefCode + " and billno " + billno);
            }

            redisTemplate.delete(orderKey);

            System.out.println("Order deleted from chef orders");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    @Async // Executes in a separate thread
    public void updateOrderStatus(String foodCode, String billno) throws IOException {
        try {
            String orderKey = "orders:bill:" + billno + ":" + foodCode;
            String chefOrderKeyPattern = "chefOrders:*:" + billno; // Matches chef orders for this bill

            Object quantityObj = redisTemplate.opsForHash().get(orderKey, "quantity");
            Object statusObj = redisTemplate.opsForHash().get(orderKey, "status");

            if (quantityObj == null) {
                System.out.println("Skipping update: Order not found for BillNo: " + billno);
                removeChefOrder(chefOrderKeyPattern); // Remove from chef orders
                return;
            }

            if (statusObj == null) {
                System.out.println("Skipping update: Order for BillNo: " + billno + " has been deleted.");
                removeChefOrder(chefOrderKeyPattern); // Remove from chef orders
                return;
            }

            int quantity = Integer.parseInt(quantityObj.toString());
            int waitTime = quantity * 2; // Delay calculation

            System.out.println("Updating status for BillNo " + billno + " after " + waitTime + " seconds...");
            TimeUnit.SECONDS.sleep(waitTime);

            redisTemplate.opsForHash().put(orderKey, "status", "Prepared");
            System.out.println("Order " + billno + " marked as Prepared.");

            // Remove from chef orders after update
            removeChefOrder(chefOrderKeyPattern);

        } catch (InterruptedException e) {
            throw new IOException("Thread interrupted while updating status", e);
        }
    }

    private void removeChefOrder(String chefOrderKeyPattern) {
        // Find all chef orders related to this bill
        var keys = redisTemplate.keys(chefOrderKeyPattern);
        if (keys != null) {
            for (String key : keys) {
                redisTemplate.delete(key);
                System.out.println("Removed chef order entry: " + key);
            }
        }
    }

    public List<Order> getOrders(String chefCode) throws IOException {
        try {
            String pattern = "chefOrders:" + chefCode + ":*"; // Match all orders for this chef
            System.out.println("Scanning Redis for keys matching: " + pattern);

            Set<String> keys = redisTemplate.keys(pattern);
            System.out.println("Found keys: " + keys);
            if (keys == null || keys.isEmpty()) {
                System.out.println("No orders found for chef: " + chefCode);
                return new ArrayList<>();
            }

            List<Order> orders = new ArrayList<>();
            for (String key : keys) {
                System.out.println("Fetching order details for key: " + key);

                Object foodCodeObj = redisTemplate.opsForHash().get(key, "foodCode");
                Object quantityObj = redisTemplate.opsForHash().get(key, "quantity");
                Object billnoObj = redisTemplate.opsForHash().get(key, "billno"); // Get BillNo

                if (foodCodeObj != null && quantityObj != null && billnoObj != null) {
                    String billno = billnoObj.toString();
                    String statusKey = "orders:bill:" + billno + ":" + foodCodeObj;
                    System.out.println(statusKey);
                    Object statusObj = redisTemplate.opsForHash().get(statusKey, "status");

                    System.out.println("Retrieved -> FoodCode: " + foodCodeObj + ", Quantity: " + quantityObj + ", Status: " + statusObj);

                    orders.add(new Order(
                            foodCodeObj.toString(),
                            Integer.parseInt(quantityObj.toString()),
                            (statusObj != null) ? statusObj.toString() : "Unknown"
                    ));
                } else {
                    System.out.println("Skipping incomplete order data for key: " + key);
                }
            }

            System.out.println("Total orders found: " + orders.size());
            return orders;
        } catch (Exception e) {
            System.out.println("Error fetching orders: " + e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

}
