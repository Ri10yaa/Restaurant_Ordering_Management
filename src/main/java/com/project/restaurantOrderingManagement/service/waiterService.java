package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.OperationFailedException;
import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.waiter.Order;
import com.project.restaurantOrderingManagement.waiter.OrderPublisher;
import com.project.restaurantOrderingManagement.waiter.tableAssignment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class waiterService {

    private final tableAssignment tableAssignment;
    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderPublisher orderPublisher;

    private static final String ORDER_KEY_PREFIX = "orders:bill:";

    public waiterService(tableAssignment tableAssignment,
                         RedisTemplate<String, Object> redisTemplate,
                         OrderPublisher orderPublisher) {
        this.tableAssignment = tableAssignment;
        this.redisTemplate = redisTemplate;
        this.orderPublisher = orderPublisher;
    }

    public List<table> fetchTables(String waiterCode) {
        return tableAssignment.getTablesByWaiterCode(waiterCode);
    }

    public String updateOrderStatus(String foodCode, long billNo) {
        try {
            String orderKey = ORDER_KEY_PREFIX + billNo + ":" + foodCode;
            Map<Object, Object> orderMap = redisTemplate.opsForHash().entries(orderKey);
            Order order = Order.mapToOrder(orderMap, foodCode);

            if ("prepared".equalsIgnoreCase(order.getStatus())) {
                order.setStatus("Served");
                redisTemplate.opsForHash().put(orderKey, "status", order.getStatus());
                orderPublisher.publishOrderUpdate(orderKey);
                return "Order marked as Served";
            }

            return "Only Prepared orders can be marked as Served";
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception e) {
            throw new OperationFailedException("Failed to update order status for bill " + billNo + " and food " + foodCode, e);
        }
    }
}
