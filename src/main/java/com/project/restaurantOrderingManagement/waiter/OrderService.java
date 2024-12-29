package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.repositories.logRepo;
import com.project.restaurantOrderingManagement.service.foodService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class OrderService {

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private final logRepo logRepo;
    @Autowired
    private final foodServiceRedis foodServiceRedis;

    public OrderService(RedisTemplate<String, Object> redisTemplate, logRepo logRepo, foodServiceRedis foodServiceRedis,foodService foodService) {
        this.redisTemplate = redisTemplate;
        this.logRepo = logRepo;
        this.foodServiceRedis = foodServiceRedis;
    }

    public void addFoodItem(long billno, String foodCode,int quantity) {
        try{
            redisTemplate.opsForList().rightPush("orders:bill" + billno, new Order(foodCode,quantity,"Queued"));
            foodServiceRedis.decrementAvailability(foodCode,quantity);
        } catch (RuntimeException e) {
            System.err.println("Error adding food item" + e.getMessage());
        }

    }

    public void removeFoodItem(long billno, Order order) {
        redisTemplate.opsForList().remove("orders:bill" + billno,1,order);
        foodServiceRedis.incrementAvailability(order.getFoodCode(),order.getQuantity());
    }

    public void updateFoodItem(long billno, String foodCode,int quantity) {
        try{
            List<Order> orders = redisTemplate.opsForList().range("orders:bill" + billno,0,-1).stream().map(obj -> (Order) obj).collect(Collectors.toList());
            if(orders.size()!=0){
                for(Order order : orders){
                    if(order.getFoodCode().equals(foodCode)){
                        int diff = quantity - order.getQuantity();
                        if(diff>0){
                            foodServiceRedis.decrementAvailability(foodCode,diff);
                            order.setQuantity(order.getQuantity()+diff);
                        }
                        else {
                            foodServiceRedis.incrementAvailability(foodCode,diff);
                            order.setQuantity(order.getQuantity()-diff);
                        }
                    }
                }
                return;
            }
            throw new RuntimeException("Order not found");
        }
        catch (RuntimeException e){
            System.err.println("Error updating food item" + e.getMessage());
        }

    }

    public List<Order> closeOrder(long billno) {
        List<Object> orders = redisTemplate.opsForList().range("orders:bill" + billno, 0, -1);
        if (orders!=null && !orders.isEmpty()) {
            redisTemplate.delete("orders:bill" + billno);
            return orders.stream().map(order -> (Order) order).collect(Collectors.toList());
        }
        else{
            return null;
        }
    }


}
