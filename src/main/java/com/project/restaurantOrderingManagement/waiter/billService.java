package com.project.restaurantOrderingManagement.waiter;
import com.project.restaurantOrderingManagement.exceptions.EntityNotFoundException;
import com.project.restaurantOrderingManagement.helpers.BillNoIncrementingService;
import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.models.Log;
import com.project.restaurantOrderingManagement.repositories.foodRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.VariableOperators;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.project.restaurantOrderingManagement.repositories.logRepo;

import java.io.*;
import java.util.*;

//check for calculate amount function
@Service
public class billService {
    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private final foodRepo foodRepo;

    public billService(com.project.restaurantOrderingManagement.repositories.logRepo logRepo, OrderService orderService, RedisTemplate<String, Object> redisTemplate, foodRepo foodRepo, BillNoIncrementingService billNoIncrementingService) {
        this.logRepo = logRepo;
        this.orderService = orderService;
        this.redisTemplate = redisTemplate;
        this.foodRepo = foodRepo;
        this.billNoIncrementingService = billNoIncrementingService;
    }

    @Autowired
    private logRepo logRepo;
    @Autowired
    private final BillNoIncrementingService billNoIncrementingService;

    private static final String key = "bill:";
    public double calculateAmount(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            System.err.println("Orders list is null or empty");
            return 0.0;
        }
        double amount = 0;
        for (Order order : orders) {
            if (order == null) {
                System.err.println("Null order found in the list");
                continue;
            }
            if (order.getFoodCode() == null || order.getFoodCode().isEmpty()) {
                System.err.println("Invalid foodCode in order: " + order);
                continue;
            }

            Optional<Food> foodOptional = foodRepo.findById(order.getFoodCode());
            if (!foodOptional.isPresent()) {
                System.err.println("Food not found for foodCode: " + order.getFoodCode());
                continue;
            }

            Food food = foodOptional.get();
            amount += food.getPrice() * order.getQuantity();
        }

        return amount;
    }

    public billDTO getBill(long billNo) throws ClassNotFoundException , IOException{
        String waitercode = (String) redisTemplate.opsForHash().get(key + billNo, "waiterCode");
        int tableNo = Integer.parseInt((String) redisTemplate.opsForHash().get(key + billNo, "tableNo"));
        if(waitercode == null || tableNo == 0){
            throw new EntityNotFoundException("Bill No is not found (redis)");
        }
        return new billDTO(billNo, waitercode, tableNo);
    }

    public long storeBill(String waitercode, String tableNo) throws IOException {
        try{
            long bill = billNoIncrementingService.incrementBillNo();
            redisTemplate.opsForHash().put(key + bill,"billNo",String.valueOf(bill));
            redisTemplate.opsForHash().put(key + bill,"waiterCode",waitercode);
            redisTemplate.opsForHash().put(key + bill,"tableNo",tableNo);
            return bill;
        }
        catch(Exception e){
            throw new RuntimeException("Error inserting bill" + e.getMessage());
        }
    }

    public List<Order> closeBill(String waiterCode,long billNo) {
        if(!redisTemplate.hasKey(key + billNo)) {
            throw new EntityNotFoundException("Bill not found");
        }
        try{
            List<Order> orders = orderService.closeOrder(billNo);
            System.out.println("In bill service");
            for(Order order : orders){
                System.out.println(order.getFoodCode());
            }
            if(orders.isEmpty()){
                throw new EntityNotFoundException("Orders are null");
            }
            double amt = calculateAmount(orders);
            System.out.println("Amount after close: " + amt);
            Date date = new Date();

            Log log = new Log();
            log.setAmount(amt);
            log.setFoodItems(orders);
            log.setBillNo(billNo);
            log.setWaiterCode(waiterCode);
            log.setDate(date);
            logRepo.save(log);
            redisTemplate.delete(key + billNo);
            return orders;
        }
        catch(Exception e){
            throw new RuntimeException("Error while closing bill : " + e.getMessage());
        }

    }

    public String deleteBill(long billNo) {
        try{
            Set<String> bill = redisTemplate.keys(key + billNo);
            if(bill.isEmpty()){
                throw new EntityNotFoundException("Bill not found");
            }
            else{
                List<Order> orders = orderService.getOrders(billNo);
                for(Order order : orders){
                    orderService.deleteOrder(billNo, order.getFoodCode());
                }
                redisTemplate.delete(key + billNo);
                return "Bill No " + billNo + " deleted";
            }
        } catch (Exception e) {
            throw new RuntimeException("Error in deleting bill" + e.getMessage());
        }
    }


}
