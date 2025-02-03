package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.repositories.logRepo;
import com.project.restaurantOrderingManagement.service.foodService;
import com.project.restaurantOrderingManagement.service.queueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.VariableOperators;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service

public class OrderService {

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private final logRepo logRepo;
    @Autowired
    private final foodServiceRedis foodServiceRedis;
    @Autowired
    private final queueService queueService;
    private final String key ="orders:bill:";

    public OrderService(RedisTemplate<String, Object> redisTemplate, logRepo logRepo, foodServiceRedis foodServiceRedis,foodService foodService,queueService queueService) {
        this.redisTemplate = redisTemplate;
        this.logRepo = logRepo;
        this.foodServiceRedis = foodServiceRedis;
        this.queueService = queueService;
    }

    private void pushOrderToQueue(String orderKey) {
        queueService.enqueue(orderKey);
    }

    public void storeOrder(long billno, Order order) throws IOException {
        try{
            String orderKey = key + billno + ":" + order.getFoodCode();
            redisTemplate.opsForHash().put(key + billno + ":" + order.getFoodCode(),"quantity",String.valueOf(order.getQuantity()));
            redisTemplate.opsForHash().put(key + billno + ":" + order.getFoodCode(),"status",order.getStatus());
            foodServiceRedis.decrementAvailability(order.getFoodCode(),order.getQuantity());
            pushOrderToQueue(orderKey);
        }catch (Exception e){
            throw new IOException("Order not inserted\n" + e.getMessage());
        }

    }

    public void removeFoodItem(long billno, String foodCode) throws IOException {
        try{
            Map<Object,Object> orderData = redisTemplate.opsForHash().entries(key + billno + ":" + (String)foodCode);
            int quantity = Integer.parseInt((String)redisTemplate.opsForHash().get(key + billno + ":" + (String) foodCode,"quantity"));
            System.out.println("Quantity : " + quantity);
            redisTemplate.delete(key + billno + ":" + foodCode);
            foodServiceRedis.incrementAvailability(foodCode,quantity);
        }
        catch (Exception e){
            throw new IOException(e.getMessage());
        }

    }

    public void updateFoodItem(long billno, Order order) throws IOException {
       try{
           Map<Object,Object> orderData = redisTemplate.opsForHash().entries(key + billno + ":" + order.getFoodCode());
           if(orderData.isEmpty()){
               throw new IOException("Order not found");
           }
           Integer quantity = Integer.parseInt((String)orderData.get("quantity"));
           System.out.println("New quantity " + order.getQuantity());
           System.out.println("Old Quantity " + quantity.getClass());
           if(quantity < order.getQuantity()){
               foodServiceRedis.decrementAvailability(order.getFoodCode(),order.getQuantity()-quantity);
           } else if (quantity > order.getQuantity()) {
               foodServiceRedis.incrementAvailability(order.getFoodCode(),quantity-order.getQuantity());
           }
           redisTemplate.opsForHash().put(key + billno + ":" + order.getFoodCode(),"quantity",String.valueOf(order.getQuantity()));
           redisTemplate.opsForHash().put(key + billno + ":" + order.getFoodCode(),"status",order.getStatus());
       }
       catch (Exception e){
           throw new IOException(e.getMessage());
       }

    }

    public List<Order> getOrders(long billno) throws IOException {
        try{
            String pattern = key + billno + ":*";
            Set<String> keys = redisTemplate.keys(pattern);
            if(keys.isEmpty()){
                return new ArrayList<>();
            }
            List<Order> orders = new ArrayList<>();
            for (String key : keys) {
                String foodCode = key.substring(key.lastIndexOf(":")+1);
                int quantity = Integer.parseInt((String) redisTemplate.opsForHash().get(key, "quantity"));
                String  status = (String) redisTemplate.opsForHash().get(key, "status");
                orders.add(new Order(foodCode, quantity, status));
            }
            return orders;
        }
        catch (Exception e){
            throw new IOException(e.getMessage());
        } 
    }

    public List<Order> closeOrder(long billno) throws IOException {
        try{
           List<Order> orders = getOrders(billno);
           boolean canClose = true;
           for (Order order : orders) {
               if(!(order.getStatus().equalsIgnoreCase("served"))){
                    canClose = false;
                    break;
               }
           }
           if(canClose){
               return orders;
           }
           else{
               return new ArrayList<>();
           }
        }
        catch (Exception e){
            throw new IOException("Failed to retrieve orders");
        }
    }



}
