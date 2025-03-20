package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.waiter.Order;
import com.project.restaurantOrderingManagement.waiter.OrderPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class waiterService {
    @Autowired
    com.project.restaurantOrderingManagement.waiter.tableAssignment tableAssignment;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderPublisher orderPublisher;

    private String key = "orders:bill:";

    public List<table> fetchTables(String waiterCode) {
        try{
            List<table> tables = tableAssignment.getTablesByWaiterCode(waiterCode);
            if(tables.isEmpty()){
                return null;
            }
            return tables;
        }
        catch (Exception e){
            throw new RuntimeException("Error fetching tables in service file" + e.getMessage());
        }
    }

    @Async
    public String updateOrderStatus(String foodCode, long billno) {
        try{
            Map<Object,Object> orderMap = redisTemplate.opsForHash().entries(key + billno + ":" +  foodCode);
            Order order = new Order();
            order = order.mapToOrder(orderMap);
            System.out.println(order);
            if(order!=null && order.getStatus().equalsIgnoreCase("prepared")){
                order.setStatus("served");
                redisTemplate.opsForHash().put(key + billno + ":" + foodCode,"status",order.getStatus());
                orderPublisher.publishOrderUpdate((key + billno + ":" + foodCode).toString());
                return "Order status updated to served!";
            }
            else{
                return "Order status cannot be updated!";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "Order status cannot be updated!";
        }
    }

}
