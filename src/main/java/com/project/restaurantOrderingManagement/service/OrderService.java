package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.OrderNotFoundException;
import com.project.restaurantOrderingManagement.repositories.logRepo;
import com.project.restaurantOrderingManagement.waiter.Order;
import com.project.restaurantOrderingManagement.waiter.OrderPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service

public class OrderService {

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private final logRepo logRepo;
    @Autowired
    private final com.project.restaurantOrderingManagement.service.foodAvailabilityService foodAvailabilityService;
    @Autowired
    private final queueService queueService;
    @Autowired
    private OrderPublisher orderPublisher;

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

    @Async
    public void storeOrder(long billno, Order order) throws IOException {
        try{
            String orderKey = key + billno + ":" + order.getFoodCode();
            foodAvailabilityService.decrementAvailability(order.getFoodCode(),order.getQuantity());
            redisTemplate.opsForHash().put(key + billno + ":" + order.getFoodCode(),"quantity",String.valueOf(order.getQuantity()));
            redisTemplate.opsForHash().put(key + billno + ":" + order.getFoodCode(),"status",order.getStatus());
            orderPublisher.publishOrderUpdate(orderKey);
            pushOrderToQueue(orderKey);
        }catch (Exception e){
            e.printStackTrace();
            throw new IOException("Order not inserted\n" + e.getMessage(), e);

        }

    }

    public String deleteOrder(long billno, String foodCode) throws IOException {
        List<Order> orders = Optional.ofNullable(this.getOrders(billno))
                .orElse(Collections.emptyList());

        if (orders.isEmpty()) {
            throw new OrderNotFoundException("No orders found.");
        }

        Optional<Order> optionalOrder = orders.stream()
                .filter(order -> order.getFoodCode().equalsIgnoreCase(foodCode))
                .findFirst();

        if (!optionalOrder.isPresent()) {
            throw new OrderNotFoundException("Food item not found in the order list.");
        }

        Order orderToUpdate = optionalOrder.get();
        orderToUpdate.setStatus("Deleted");
        orderPublisher.publishOrderDelete(key + billno + ":" + orderToUpdate.getFoodCode());
        this.updateFoodItem(billno, orderToUpdate);
        return "Order status updated to Deleted.";
    }

    @Async
    public Order updateFoodItem(long billno, Order order) throws IOException {
            Map<Object,Object> orderData = redisTemplate.opsForHash().entries(key + billno + ":" + order.getFoodCode());
            if(orderData.isEmpty()){
                throw new OrderNotFoundException("Order not found\n");
            }
            int quantity = Integer.parseInt(orderData.get("quantity").toString());
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
            orderPublisher.publishOrderUpdate(key + billno + ":" + order.getFoodCode());
            return order;

    }


    public List<Order> getOrders(long billno) throws IOException {
            String pattern = key + billno + ":*";
            Set<String> keys = redisTemplate.keys(pattern);
            if(keys.isEmpty()){
              return null;
            }
            List<Order> orders = new ArrayList<>();
            for (String key : keys) {
                String foodCode = key.substring(key.lastIndexOf(":")+1);
                int quantity = Integer.parseInt(redisTemplate.opsForHash().get(key, "quantity").toString());
                String  status = (String) redisTemplate.opsForHash().get(key, "status");
                String chefCode = (String) redisTemplate.opsForHash().get(key, "chefCode");
                orders.add(new Order(foodCode, quantity, status,chefCode));
            }

            return orders;
    }

    public List<Order> closeOrder(long billno) throws RuntimeException {
        try{
            System.out.println("Entered close order\n");
           List<Order> orders = getOrders(billno);
           System.out.println("List of orders");
           if(orders.isEmpty()){
                throw new OrderNotFoundException("Order not found");
           }
           else{
               for(Order order : orders){
                   System.out.println(order.getFoodCode());
               }
               List<Order> closedOrders = new ArrayList<>();
               boolean canClose = true;
               for (Order order : orders) {
                   System.out.println(order.getStatus());
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
