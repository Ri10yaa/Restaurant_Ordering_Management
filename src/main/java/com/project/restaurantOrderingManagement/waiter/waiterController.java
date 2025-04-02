package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.exceptions.FoodNotFoundException;
import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.models.Log;
import com.project.restaurantOrderingManagement.models.foodWithAvailability;
import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.service.OrderService;
import com.project.restaurantOrderingManagement.service.billService;
import com.project.restaurantOrderingManagement.service.foodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
//done testing
@RestController
@RequestMapping("/{waitercode}")
public class waiterController {
    @Autowired
    com.project.restaurantOrderingManagement.service.waiterService waiterService;
    @Autowired
    billService billService;
    @Autowired
    OrderService orderService;
    @Autowired
    foodService foodService;

    //fetch table
    @GetMapping("/fetchTables")
    public ResponseEntity<List<table>> fetchTables(@PathVariable("waitercode") String waitercode) {
        List<table> tables = waiterService.fetchTables(waitercode);
        if (tables.isEmpty()) {
            ResponseEntity.status(500).body("Tables not found");
        }
        return ResponseEntity.ok(tables);
    }
    //Create bill
    //Give tableNo alone in response body
    @PostMapping("/bill/create")
    public ResponseEntity<Long> createBill(@RequestBody Map<String,String> requestBody,@PathVariable("waitercode") String waitercode) throws IOException {
        if (waitercode == null) {
            throw new IllegalArgumentException("waiterCode cannot be null or empty");
        }
        Long billNo = billService.storeBill(waitercode,requestBody.get("tableNo"),requestBody.get("persons"));
        return ResponseEntity.ok(billNo);
    }
    //get bill
    @GetMapping("/get/{billNo}")
    public ResponseEntity<billDTO> getBill(@PathVariable("billNo") String billNo) throws IOException, ClassNotFoundException {
        billDTO bill = billService.getBill(Long.parseLong(billNo));
        return ResponseEntity.ok(bill);

    }
    //close bill
    // Request body contains billNo
    @PostMapping("/bill/close")
    public ResponseEntity<Log> closeBill(@RequestBody Map<String, String> requestBody,@PathVariable("waitercode") String waitercode) throws IOException {
        Log l = billService.closeBill(waitercode,Long.parseLong(requestBody.get("billNo")));
        return ResponseEntity.ok(l);

    }
    //delete bill
    @DeleteMapping("/bill/{billNo}")
    public ResponseEntity<?> deleteBill(@PathVariable("billNo") Long billNo) throws IOException {
        try{
            String response = billService.deleteBill(billNo);
            return ResponseEntity.ok(response);
        }catch(Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    //create order
    // RequestBody "foodCode, quantity, status"
    @PostMapping("/order/{billno}")
    public ResponseEntity<Object> createOrder(@PathVariable String billno, @RequestBody Order order) throws IOException {
       try{
           orderService.storeOrder(Long.parseLong(billno), order);
           List<Order> updatedOrders = orderService.getOrders(Long.parseLong(billno));
       }
       catch (Exception e){
           return ResponseEntity.status(500).body(e.getMessage());
       }
        return ResponseEntity.ok().build();
    }

    //search food
    @GetMapping("/search")
    public List<foodWithAvailability> searchFood(@RequestParam String keyword) {
        try {
            return foodService.searchFoodByCodeOrName(keyword);
        } catch (FoodNotFoundException e) {
            throw new FoodNotFoundException(e.getMessage());
        }
    }
    //update order
    @PutMapping("/order/{billno}")
    public ResponseEntity<Object> updateOrder(@PathVariable("billno") String billno, @RequestBody Order order) throws IOException {
        Order newOrder = null;
        try {
            newOrder = orderService.updateFoodItem(Long.parseLong(billno), order);
            List<Order> updatedOrders = orderService.getOrders(Long.parseLong(billno));
        } catch (Exception e) {
             return ResponseEntity.status(500).body(e.getMessage());
        }
        return ResponseEntity.ok(newOrder);
    }

    //update order status
    @PostMapping("/order/{billno}/serve")
    public ResponseEntity<Object> updateOrderStatus(@PathVariable("billno") String billno, @RequestBody Map<String,String> requestBody) throws IOException {
        try {
            String foodCode = requestBody.get("foodCode");
            String msg = waiterService.updateOrderStatus(foodCode, Long.parseLong(billno));
            return ResponseEntity.ok(msg);
        }catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    //view order
    @GetMapping("/order/{billno}")
    public ResponseEntity<List<Order>> getOrders(@PathVariable("billno") String billno) throws IOException {
        List<Order> orders = orderService.getOrders(Long.parseLong(billno));
        if(orders.isEmpty()) {
            return null;
        }
        return ResponseEntity.ok(orders);
    }
    //delete order
    @DeleteMapping("/order/{billno}")
    public ResponseEntity<Object> deleteOrder(@PathVariable("billno") String billno,
                                              @RequestBody Map<String, String> requestBody) {
        System.out.println("Received request to delete food item.");
        System.out.println("Bill No: " + billno);
        System.out.println("Request Body: " + requestBody);

        String foodCode = requestBody.get("foodCode");
        if (foodCode == null || foodCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("FoodCode is missing in request body.");
        }

        try {
            String responseMessage = orderService.deleteOrder(Long.parseLong(billno), foodCode);
            return ResponseEntity.ok(responseMessage);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
