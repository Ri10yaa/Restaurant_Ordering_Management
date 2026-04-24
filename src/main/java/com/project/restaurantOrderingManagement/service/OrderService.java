package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.BadRequestException;
import com.project.restaurantOrderingManagement.exceptions.ConflictException;
import com.project.restaurantOrderingManagement.exceptions.OperationFailedException;
import com.project.restaurantOrderingManagement.exceptions.OrderNotFoundException;
import com.project.restaurantOrderingManagement.repositories.logRepo;
import com.project.restaurantOrderingManagement.waiter.Order;
import com.project.restaurantOrderingManagement.waiter.OrderPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class OrderService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final logRepo logRepo;
    private final foodAvailabilityService foodAvailabilityService;
    private final queueService queueService;
    private final OrderPublisher orderPublisher;

    private static final String KEY_PREFIX = "orders:bill:";

    public OrderService(RedisTemplate<String, Object> redisTemplate,
                        logRepo logRepo,
                        foodAvailabilityService foodAvailabilityService,
                        queueService queueService,
                        OrderPublisher orderPublisher) {
        this.redisTemplate = redisTemplate;
        this.logRepo = logRepo;
        this.foodAvailabilityService = foodAvailabilityService;
        this.queueService = queueService;
        this.orderPublisher = orderPublisher;
    }

    public void storeOrder(long billNo, Order order) {
        validateOrderInput(order);
        String orderKey = KEY_PREFIX + billNo + ":" + order.getFoodCode();

        try {
            foodAvailabilityService.decrementAvailability(order.getFoodCode(), order.getQuantity());
            redisTemplate.opsForHash().put(orderKey, "quantity", String.valueOf(order.getQuantity()));
            redisTemplate.opsForHash().put(orderKey, "status", order.getStatus());
            orderPublisher.publishOrderUpdate(orderKey);
            queueService.enqueue(orderKey);
        } catch (IOException e) {
            throw new OperationFailedException("Unable to place order due to stock issue for " + order.getFoodCode(), e);
        } catch (Exception e) {
            throw new OperationFailedException("Failed to store order for bill " + billNo, e);
        }
    }

    public String deleteOrder(long billNo, String foodCode) {
        if (foodCode == null || foodCode.isBlank()) {
            throw new BadRequestException("foodCode is required to delete order");
        }

        Order orderToDelete = getOrderOrThrow(billNo, foodCode);
        orderToDelete.setStatus("Deleted");
        updateFoodItem(billNo, orderToDelete);
        orderPublisher.publishOrderDelete(KEY_PREFIX + billNo + ":" + foodCode);
        return "Order status updated to Deleted";
    }

    public Order updateFoodItem(long billNo, Order order) {
        validateOrderInput(order);
        String orderKey = KEY_PREFIX + billNo + ":" + order.getFoodCode();
        Map<Object, Object> orderData = redisTemplate.opsForHash().entries(orderKey);

        if (orderData == null || orderData.isEmpty()) {
            throw new OrderNotFoundException("Order not found for bill " + billNo + " and food " + order.getFoodCode());
        }

        int currentQuantity = Integer.parseInt(orderData.get("quantity").toString());
        int newQuantity = order.getQuantity();

        try {
            if (newQuantity > currentQuantity) {
                foodAvailabilityService.decrementAvailability(order.getFoodCode(), newQuantity - currentQuantity);
            } else if (newQuantity < currentQuantity) {
                foodAvailabilityService.incrementAvailability(order.getFoodCode(), currentQuantity - newQuantity);
            }

            redisTemplate.opsForHash().put(orderKey, "quantity", String.valueOf(newQuantity));
            redisTemplate.opsForHash().put(orderKey, "status", order.getStatus());
            orderPublisher.publishOrderUpdate(orderKey);
            return order;
        } catch (IOException e) {
            throw new OperationFailedException("Insufficient stock while updating order for " + order.getFoodCode(), e);
        } catch (Exception e) {
            throw new OperationFailedException("Failed to update order for bill " + billNo, e);
        }
    }

    public List<Order> getOrders(long billNo) {
        String pattern = KEY_PREFIX + billNo + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        List<Order> orders = new ArrayList<>();
        for (String redisKey : keys) {
            Map<Object, Object> orderMap = redisTemplate.opsForHash().entries(redisKey);
            if (orderMap == null || orderMap.isEmpty()) {
                continue;
            }
            String foodCode = redisKey.substring(redisKey.lastIndexOf(':') + 1);
            orders.add(Order.mapToOrder(orderMap, foodCode));
        }

        return orders;
    }

    public List<Order> closeOrder(long billNo) {
        List<Order> orders = getOrders(billNo);
        if (orders.isEmpty()) {
            throw new OrderNotFoundException("No active orders found for bill " + billNo);
        }

        List<Order> finalOrders = new ArrayList<>();
        for (Order order : orders) {
            String status = order.getStatus() == null ? "" : order.getStatus().trim().toLowerCase();

            if (status.equals("served")) {
                finalOrders.add(order);
            } else if (!status.equals("deleted")) {
                throw new ConflictException("Cannot close bill " + billNo + " because order " + order.getFoodCode() + " is in status " + order.getStatus());
            }

            redisTemplate.delete(KEY_PREFIX + billNo + ":" + order.getFoodCode());
        }

        return finalOrders;
    }

    private Order getOrderOrThrow(long billNo, String foodCode) {
        List<Order> orders = getOrders(billNo);
        return orders.stream()
                .filter(order -> foodCode.equalsIgnoreCase(order.getFoodCode()))
                .findFirst()
                .orElseThrow(() -> new OrderNotFoundException("Food item " + foodCode + " not found in bill " + billNo));
    }

    private void validateOrderInput(Order order) {
        if (order == null) {
            throw new BadRequestException("Order payload is required");
        }
        if (order.getFoodCode() == null || order.getFoodCode().isBlank()) {
            throw new BadRequestException("foodCode is required");
        }
        if (order.getQuantity() <= 0) {
            throw new BadRequestException("quantity must be greater than 0");
        }
        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("Ordered");
        }
    }
}
