package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.exceptions.BadRequestException;
import com.project.restaurantOrderingManagement.models.Log;
import com.project.restaurantOrderingManagement.models.foodWithAvailability;
import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.service.OrderService;
import com.project.restaurantOrderingManagement.service.billService;
import com.project.restaurantOrderingManagement.service.foodService;
import com.project.restaurantOrderingManagement.service.waiterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/{waiterCode}")
public class waiterController {

    private final waiterService waiterService;
    private final billService billService;
    private final OrderService orderService;
    private final foodService foodService;

    public waiterController(waiterService waiterService,
                            billService billService,
                            OrderService orderService,
                            foodService foodService) {
        this.waiterService = waiterService;
        this.billService = billService;
        this.orderService = orderService;
        this.foodService = foodService;
    }

    @GetMapping("/fetchTables")
    public ResponseEntity<List<table>> fetchTables(@PathVariable String waiterCode) {
        return ResponseEntity.ok(waiterService.fetchTables(waiterCode));
    }

    @PostMapping("/bill/create")
    public ResponseEntity<Long> createBill(@RequestBody Map<String, String> requestBody,
                                           @PathVariable String waiterCode) {
        String tableNo = requestBody.get("tableNo");
        String persons = requestBody.get("persons");
        return ResponseEntity.ok(billService.storeBill(waiterCode, tableNo, persons));
    }

    @GetMapping("/bill/{billNo}")
    public ResponseEntity<billDTO> getBill(@PathVariable long billNo) {
        return ResponseEntity.ok(billService.getBill(billNo));
    }

    @PostMapping("/bill/close")
    public ResponseEntity<Log> closeBill(@RequestBody Map<String, String> requestBody,
                                         @PathVariable String waiterCode) {
        String billNo = requestBody.get("billNo");
        if (billNo == null || billNo.isBlank()) {
            throw new BadRequestException("billNo is required");
        }
        return ResponseEntity.ok(billService.closeBill(waiterCode, Long.parseLong(billNo)));
    }

    @DeleteMapping("/bill/{billNo}")
    public ResponseEntity<String> deleteBill(@PathVariable long billNo) {
        return ResponseEntity.ok(billService.deleteBill(billNo));
    }

    @PostMapping("/order/{billNo}")
    public ResponseEntity<String> createOrder(@PathVariable long billNo, @RequestBody Order order) {
        orderService.storeOrder(billNo, order);
        return ResponseEntity.ok("Order created");
    }

    @PutMapping("/order/{billNo}")
    public ResponseEntity<Order> updateOrder(@PathVariable long billNo, @RequestBody Order order) {
        return ResponseEntity.ok(orderService.updateFoodItem(billNo, order));
    }

    @PostMapping("/order/{billNo}/serve")
    public ResponseEntity<String> updateOrderStatus(@PathVariable long billNo,
                                                    @RequestBody Map<String, String> requestBody) {
        String foodCode = requestBody.get("foodCode");
        if (foodCode == null || foodCode.isBlank()) {
            throw new BadRequestException("foodCode is required");
        }
        return ResponseEntity.ok(waiterService.updateOrderStatus(foodCode, billNo));
    }

    @GetMapping("/order/{billNo}")
    public ResponseEntity<List<Order>> getOrders(@PathVariable long billNo) {
        return ResponseEntity.ok(orderService.getOrders(billNo));
    }

    @DeleteMapping("/order/{billNo}")
    public ResponseEntity<String> deleteOrder(@PathVariable long billNo,
                                              @RequestBody Map<String, String> requestBody) {
        String foodCode = requestBody.get("foodCode");
        return ResponseEntity.ok(orderService.deleteOrder(billNo, foodCode));
    }

    @GetMapping("/search")
    public ResponseEntity<List<foodWithAvailability>> searchFood(@RequestParam String keyword) {
        return ResponseEntity.ok(foodService.searchFoodByCodeOrName(keyword));
    }
}
