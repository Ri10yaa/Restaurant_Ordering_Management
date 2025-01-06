package com.project.restaurantOrderingManagement.waiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/order")
public class orderController {
    @Autowired
    private final OrderService orderService;
    public orderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{billno}")
    public ResponseEntity<List<Order>> getOrders(@PathVariable("billno") String billno) throws IOException {
        List<Order> orders = orderService.getOrders(Long.parseLong(billno));
        if(orders.isEmpty()) {
            return null;
        }
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{billno}")
    public ResponseEntity<Object> createOrder(@PathVariable String billno, @RequestBody Order order) throws IOException {
        System.out.println("Foodcode : " + order.getFoodCode());
        System.out.println("Quantity : " + order.getQuantity());
        System.out.println("Status : " + order.getStatus());
        orderService.storeOrder(Long.parseLong(billno), order);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{billno}")
    public ResponseEntity<Object> updateOrder(@PathVariable("billno") String billno, @RequestBody Order order) throws IOException {
        orderService.updateFoodItem(Long.parseLong(billno), order);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{billno}")
    public ResponseEntity<Object> deleteOrder(@PathVariable("billno") String billno, @RequestBody String foodCode) throws IOException {
        System.out.println("Foodcode : " + foodCode);
        orderService.removeFoodItem(Long.parseLong(billno), foodCode);
        return ResponseEntity.ok().build();
    }

}
