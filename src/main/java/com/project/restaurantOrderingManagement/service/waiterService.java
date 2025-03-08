package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.models.Table;
import com.project.restaurantOrderingManagement.waiter.Order;
import com.project.restaurantOrderingManagement.waiter.OrderPublisher;
import com.project.restaurantOrderingManagement.waiter.tableAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    public List<Table> fetchTables(String waiterCode) {
        try{
            List<Table> tables = tableAssignment.getTablesByWaiterCode(waiterCode);
            if(tables.isEmpty()){
                return null;
            }
            return tables;
        }
        catch (Exception e){
            throw new RuntimeException("Error fetching tables in service file" + e.getMessage());
        }
    }

    public String updateOrderStatus(String foodCode, long billno) {
        Map<Object,Object> orderMap = redisTemplate.opsForHash().entries(key + billno + foodCode);
        Order order = null;
        order = order.mapToOrder(orderMap);
        if(order.getStatus().equalsIgnoreCase("prepared")){
            order.setStatus("served");
            redisTemplate.opsForHash().put(key + billno + ":" + order.getFoodCode(),"status",order.getStatus());
            orderPublisher.publishOrderUpdate((key + billno + ":" + order.getFoodCode()).toString());
            return "Order status updated to served!";
        }
        else{
            return "Order status cannot be updated!";
        }
    }
}
