package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.BadRequestException;
import com.project.restaurantOrderingManagement.exceptions.BillNotFoundException;
import com.project.restaurantOrderingManagement.exceptions.OperationFailedException;
import com.project.restaurantOrderingManagement.exceptions.OrderNotFoundException;
import com.project.restaurantOrderingManagement.helpers.BillNoIncrementingService;
import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.models.Log;
import com.project.restaurantOrderingManagement.repositories.foodRepo;
import com.project.restaurantOrderingManagement.repositories.logRepo;
import com.project.restaurantOrderingManagement.waiter.Order;
import com.project.restaurantOrderingManagement.waiter.billDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class billService {

    private static final String BILL_KEY_PREFIX = "bill:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderService orderService;
    private final foodRepo foodRepo;
    private final tableStatusService tableStatusService;
    private final logRepo logRepo;
    private final BillNoIncrementingService billNoIncrementingService;

    public billService(logRepo logRepo,
                       OrderService orderService,
                       RedisTemplate<String, Object> redisTemplate,
                       foodRepo foodRepo,
                       tableStatusService tableStatusService,
                       BillNoIncrementingService billNoIncrementingService) {
        this.logRepo = logRepo;
        this.orderService = orderService;
        this.redisTemplate = redisTemplate;
        this.foodRepo = foodRepo;
        this.tableStatusService = tableStatusService;
        this.billNoIncrementingService = billNoIncrementingService;
    }

    public double calculateAmount(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return 0.0;
        }

        double amount = 0.0;
        for (Order order : orders) {
            if (order == null || order.getFoodCode() == null || order.getFoodCode().isBlank()) {
                continue;
            }

            Optional<Food> foodOptional = foodRepo.findById(order.getFoodCode());
            if (foodOptional.isPresent()) {
                amount += foodOptional.get().getPrice() * order.getQuantity();
            }
        }

        return amount;
    }

    public billDTO getBill(long billNo) {
        Map<Object, Object> bill = redisTemplate.opsForHash().entries(BILL_KEY_PREFIX + billNo);
        if (bill == null || bill.isEmpty()) {
            throw new BillNotFoundException("Bill " + billNo + " not found");
        }

        String waiterCode = (String) bill.get("waiterCode");
        int tableNo = Integer.parseInt(String.valueOf(bill.get("tableNo")));
        int persons = Integer.parseInt(String.valueOf(bill.get("NoOfPersons")));

        return new billDTO(billNo, waiterCode, tableNo, persons);
    }

    public long storeBill(String waiterCode, String tableNo, String persons) {
        validateBillCreateInput(waiterCode, tableNo, persons);

        try {
            long billNo = billNoIncrementingService.incrementBillNo();
            String billKey = BILL_KEY_PREFIX + billNo;

            redisTemplate.opsForHash().put(billKey, "billNo", String.valueOf(billNo));
            redisTemplate.opsForHash().put(billKey, "waiterCode", waiterCode);
            redisTemplate.opsForHash().put(billKey, "tableNo", tableNo);
            redisTemplate.opsForHash().put(billKey, "NoOfPersons", persons);

            tableStatusService.markEngaged(Integer.parseInt(tableNo), Integer.parseInt(persons));
            return billNo;
        } catch (Exception e) {
            throw new OperationFailedException("Failed to create bill for table " + tableNo, e);
        }
    }

    public Log closeBill(String waiterCode, long billNo) {
        String billKey = BILL_KEY_PREFIX + billNo;
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(billKey))) {
            throw new BillNotFoundException("Bill " + billNo + " not found");
        }

        try {
            List<Order> servedOrders = orderService.closeOrder(billNo);
            if (servedOrders.isEmpty()) {
                throw new OrderNotFoundException("No served orders available to close bill " + billNo);
            }

            Object table = redisTemplate.opsForHash().get(billKey, "tableNo");
            Object persons = redisTemplate.opsForHash().get(billKey, "NoOfPersons");

            Log log = new Log();
            log.setAmount(calculateAmount(servedOrders));
            log.setFoodItems(servedOrders);
            log.setBillNo(billNo);
            log.setWaiterCode(waiterCode);
            log.setDate(LocalDate.now());
            log.setTime(LocalTime.now());

            logRepo.save(log);

            tableStatusService.markVacant(Integer.parseInt(String.valueOf(table)), Integer.parseInt(String.valueOf(persons)));
            redisTemplate.delete(billKey);
            return log;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception e) {
            throw new OperationFailedException("Failed to close bill " + billNo, e);
        }
    }

    public String deleteBill(long billNo) {
        String billKey = BILL_KEY_PREFIX + billNo;
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(billKey))) {
            throw new BillNotFoundException("Bill " + billNo + " not found");
        }

        try {
            Object tableObj = redisTemplate.opsForHash().get(billKey, "tableNo");
            Object personsObj = redisTemplate.opsForHash().get(billKey, "NoOfPersons");

            List<Order> orders = orderService.getOrders(billNo);
            for (Order order : orders) {
                orderService.deleteOrder(billNo, order.getFoodCode());
            }

            if (tableObj != null && personsObj != null) {
                tableStatusService.markVacant(Integer.parseInt(tableObj.toString()), Integer.parseInt(personsObj.toString()));
            }
            redisTemplate.delete(billKey);

            return "Bill " + billNo + " deleted";
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception e) {
            throw new OperationFailedException("Failed to delete bill " + billNo, e);
        }
    }

    private void validateBillCreateInput(String waiterCode, String tableNo, String persons) {
        if (waiterCode == null || waiterCode.isBlank()) {
            throw new BadRequestException("waiterCode is required");
        }
        if (tableNo == null || tableNo.isBlank()) {
            throw new BadRequestException("tableNo is required");
        }
        if (persons == null || persons.isBlank()) {
            throw new BadRequestException("persons is required");
        }

        try {
            int table = Integer.parseInt(tableNo);
            int headCount = Integer.parseInt(persons);
            if (table <= 0 || headCount <= 0) {
                throw new BadRequestException("tableNo and persons must be greater than 0");
            }
        } catch (NumberFormatException ex) {
            throw new BadRequestException("tableNo and persons must be numbers");
        }
    }
}
