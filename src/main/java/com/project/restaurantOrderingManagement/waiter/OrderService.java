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
import java.util.*;
import java.util.stream.Collectors;

@Service

public class OrderService {

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private final logRepo logRepo;
    @Autowired
    private final foodAvailabilityService foodAvailabilityService;
    @Autowired
    private final queueService queueService;
    private final String key ="orders:bill:";

    public OrderService(RedisTemplate<String, Object> redisTemplate, logRepo logRepo, foodAvailabilityService foodAvailabilityService,queueService queueService) {
        this.redisTemplate = redisTemplate;
        this.logRepo = logRepo;
        this.foodAvailabilityService = foodAvailabilityService;
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
            foodAvailabilityService.decrementAvailability(order.getFoodCode(),order.getQuantity());
            pushOrderToQueue(orderKey);
        }catch (Exception e){
            throw new IOException("Order not inserted\n" + e.getMessage());
        }

    }
    public String deleteOrder(long billno, String foodCode) throws IOException {
        List<Order> orders = Optional.ofNullable(this.getOrders(billno))
                .orElse(Collections.emptyList());

        if (orders.isEmpty()) {
            throw new NoSuchElementException("No orders found.");
        }

        Optional<Order> optionalOrder = orders.stream()
                .filter(order -> order.getFoodCode().equalsIgnoreCase(foodCode))
                .findFirst();

        if (!optionalOrder.isPresent()) {
            throw new NoSuchElementException("Food item not found.");
        }

        Order orderToUpdate = optionalOrder.get();
        orderToUpdate.setStatus("Deleted");
        this.updateFoodItem(billno, orderToUpdate);

        return "Order status updated to Deleted.";
    }

    public Order updateFoodItem(long billno, Order order) throws IOException {
        try{
            Map<Object,Object> orderData = redisTemplate.opsForHash().entries(key + billno + ":" + order.getFoodCode());
            if(orderData.isEmpty()){
                throw new IOException("Order not found");
            }
            Integer quantity = Integer.parseInt((String)orderData.get("quantity"));
            System.out.println("New quantity " + order.getQuantity());
            System.out.println("Old Quantity " + quantity);
            if(quantity < order.getQuantity()){
                System.out.println("Entered decrease quantity");
                foodAvailabilityService.decrementAvailability(order.getFoodCode(),order.getQuantity()-quantity);
            } else if (quantity > order.getQuantity()) {
                foodAvailabilityService.incrementAvailability(order.getFoodCode(),quantity-order.getQuantity());
            }
            redisTemplate.opsForHash().put(key + billno + ":" + order.getFoodCode(),"quantity",String.valueOf(order.getQuantity()));
            redisTemplate.opsForHash().put(key + billno + ":" + order.getFoodCode(),"status",order.getStatus());
            return order;
        }
        catch (Exception e){
            throw new IOException("Order not updated" + e.getMessage());
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
            for(Order order : orders){
                System.out.println(order.getFoodCode());
            }
            return orders;
        }
        catch (Exception e){
            throw new IOException(e.getMessage());
        } 
    }

    public List<Order> closeOrder(long billno) throws RuntimeException {
        try{
            System.out.println("Entered close order\n");
           List<Order> orders = getOrders(billno);
           System.out.println("List of orders");
           if(orders.isEmpty()){
                throw new RuntimeException("Order not found");
           }
           else{
               for(Order order : orders){
                   System.out.println(order.getFoodCode());
               }
               List<Order> closedOrders = new ArrayList<>();
               boolean canClose = true;
               for (Order order : orders) {
                   if(order.getStatus().equalsIgnoreCase("served")){
                       closedOrders.add(order);
                   }
                   redisTemplate.delete(key + billno + ":" + order.getFoodCode());
               }

               System.out.println("List of closed orders");
               for(Order order : closedOrders){
                   System.out.println(order.getFoodCode());
               }
               return closedOrders;
           }

        }
        catch (Exception e){
            throw new RuntimeException("Failed to retrieve orders");
        }
    }



}
