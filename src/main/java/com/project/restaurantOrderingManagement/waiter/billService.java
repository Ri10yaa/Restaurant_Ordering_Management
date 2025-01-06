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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        double amount = 0;
        for (Order order : orders) {
            Food food =  (Food) foodRepo.findById(order.getFoodCode()).get();
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

    public long storeBill(billDTO billDTO) throws IOException {
        long bill = billNoIncrementingService.incrementBillNo();
        billDTO.setBillNo(bill);
        System.out.println(billDTO.getBillNo());
        System.out.println(billDTO.getWaiterCode());
        System.out.println(billDTO.getTableNo());
        redisTemplate.opsForHash().put(key + bill,"billNo",String.valueOf(billDTO.getBillNo()));
        redisTemplate.opsForHash().put(key + bill,"waiterCode",billDTO.getWaiterCode());
        redisTemplate.opsForHash().put(key + bill,"tableNo",String.valueOf(billDTO.getTableNo()));
        return bill;
    }

    public void closeBill(long billNo) {
        if(!redisTemplate.hasKey(key + billNo)) {
            throw new EntityNotFoundException("Bill not found");
        }
        try{
            billDTO bill = getBill(billNo);
            List<Order> orders = orderService.closeOrder(billNo);
            double amt = calculateAmount(orders);
            Date date = new Date();

            Log log = new Log();
            log.setAmount(amt);
            log.setFoodItems(orders);
            log.setBillNo(billNo);
            log.setWaiterCode(bill.getWaiterCode());
            log.setDate(date);
            logRepo.save(log);
            redisTemplate.delete(key + billNo);
        }
        catch(Exception e){
            throw new RuntimeException("Error while closing bill");
        }


    }

}
