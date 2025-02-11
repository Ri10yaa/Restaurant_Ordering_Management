package com.project.restaurantOrderingManagement.kitchen;

import com.project.restaurantOrderingManagement.waiter.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/chef")
public class kitchenController {

    @Autowired
    private kitchenService kitchenService;

    @PostMapping("/{empCode}")
    public String updateOrderStatus(
            @PathVariable String empCode,
            @RequestBody OrderRequest request) {  // Accept JSON body
        try {
            if(request.getBillno()!=null && request.getFoodCode()!=null) {
                kitchenService.updateOrderStatus(request.getFoodCode(), request.getBillno());
                return "Order status update initiated for BillNo: " + request.getBillno() + " by Chef: " + empCode;
            }
            return "Bill no or foodCode is null";
        } catch (IOException e) {
            return "Error updating order status: " + e.getMessage();
        }
    }


    @GetMapping("/{empCode}")
    public ResponseEntity<List<Order>> getChefOrders(@PathVariable String empCode) {
        try {
            List<Order> orders = kitchenService.getOrders(empCode);
            if (orders.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

//            StringBuilder orderDetails = new StringBuilder("Orders assigned to Chef " + empCode + ": \n");
//            for (Order order : orders) {
//                orderDetails.append("FoodCode: ").append(order.getFoodCode())
//                        .append(", Quantity: ").append(order.getQuantity())
//                        .append(", Status: ").append(order.getStatus())
//                        .append("\n");
//            } Test this controller and delete this portion after checking

            return ResponseEntity.ok(orders);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{empcode}")
    public ResponseEntity<String> deleteOrder(@PathVariable String empCode) {
        return (ResponseEntity<String>) ResponseEntity.ok();
    }
}
