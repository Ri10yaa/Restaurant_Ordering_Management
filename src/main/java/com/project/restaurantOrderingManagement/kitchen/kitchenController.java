package com.project.restaurantOrderingManagement.kitchen;

import com.project.restaurantOrderingManagement.waiter.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
            kitchenService.updateOrderStatus(request.getFoodCode(), request.getBillno());
            return "Order status update initiated for BillNo: " + request.getBillno() + " by Chef: " + empCode;
        } catch (IOException e) {
            return "Error updating order status: " + e.getMessage();
        }
    }


    @GetMapping("/{empCode}")
    public ResponseEntity<?> getChefOrders(@PathVariable String empCode) {
        try {
            List<Order> orders = kitchenService.getOrders(empCode);
            if (orders.isEmpty()) {
                return ResponseEntity.ok("No orders assigned to chef with EmpCode " + empCode);
            }

            StringBuilder orderDetails = new StringBuilder("Orders assigned to Chef " + empCode + ": \n");
            for (Order order : orders) {
                orderDetails.append("FoodCode: ").append(order.getFoodCode())
                        .append(", Quantity: ").append(order.getQuantity())
                        .append(", Status: ").append(order.getStatus())
                        .append("\n");
            }

            return ResponseEntity.ok(orderDetails.toString());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error fetching orders for chef: " + e.getMessage());
        }
    }
}
