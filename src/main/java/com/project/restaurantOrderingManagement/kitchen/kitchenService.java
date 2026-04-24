package com.project.restaurantOrderingManagement.kitchen;

import com.project.restaurantOrderingManagement.exceptions.OperationFailedException;
import com.project.restaurantOrderingManagement.exceptions.OrderNotFoundException;
import com.project.restaurantOrderingManagement.waiter.OrderPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class kitchenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderPublisher orderPublisher;

    public kitchenService(RedisTemplate<String, Object> redisTemplate, OrderPublisher orderPublisher) {
        this.redisTemplate = redisTemplate;
        this.orderPublisher = orderPublisher;
    }

    @Async
    public void updateOrderStatus(String foodCode, String billNo) {
        String orderKey = "orders:bill:" + billNo + ":" + foodCode;
        Object quantityObj = redisTemplate.opsForHash().get(orderKey, "quantity");
        if (quantityObj == null) {
            throw new OrderNotFoundException("Order not found for bill " + billNo + " and food " + foodCode);
        }

        try {
            int quantity = Integer.parseInt(quantityObj.toString());
            int waitTime = quantity * 2;
            TimeUnit.SECONDS.sleep(waitTime);

            redisTemplate.opsForHash().put(orderKey, "status", "Prepared");
            orderPublisher.publishOrderUpdate(orderKey);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OperationFailedException("Order preparation interrupted for bill " + billNo, e);
        } catch (Exception e) {
            throw new OperationFailedException("Failed to update order status for bill " + billNo, e);
        }
    }
}
