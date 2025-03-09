package com.project.restaurantOrderingManagement.admin;

import com.project.restaurantOrderingManagement.helpers.BillNoIncrementingService;
import com.project.restaurantOrderingManagement.service.foodAvailabilityService;
import com.project.restaurantOrderingManagement.waiter.tableAssignment;
import com.project.restaurantOrderingManagement.service.tableStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
//This controller for testing, not for API
@RestController
@RequestMapping("/schedule")
public class scheduledController {
    @Autowired
    private final tableAssignment tableAssignment;
    @Autowired
    private final foodAvailabilityService foodAvailabilityService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private BillNoIncrementingService billNoIncrementingService;
    @Autowired
    private tableStatusService tableStatusService;

    public scheduledController(com.project.restaurantOrderingManagement.waiter.tableAssignment tableAssignment, com.project.restaurantOrderingManagement.service.foodAvailabilityService foodAvailabilityService, BillNoIncrementingService billNoIncrementingService) {
        this.tableAssignment = tableAssignment;
        this.foodAvailabilityService = foodAvailabilityService;
        this.billNoIncrementingService = billNoIncrementingService;
    }
    //When the manager switch on the server
    @GetMapping("/assignTable")
    public ResponseEntity<?> assignTable() {
        try{
            Map<String, List<Integer>> assignment = tableAssignment.assignTable();
            return ResponseEntity.ok(assignment);
        }
        catch (Exception e){
            return ResponseEntity.status(500).body("Error assigning tables: " + e.getMessage());
        }
    }
    //when the manager switch off the server
    @GetMapping("/reset")
    public ResponseEntity<?> resetFoodAvailability() {
        try{
            foodAvailabilityService.resetAvailability();
            billNoIncrementingService.setBillNo();
            tableStatusService.resetTableStatus();
            return ResponseEntity.ok().body("Rest is successfull");
        }
        catch (Exception e){
            return ResponseEntity.status(500).body("Error resetting redis: " + e.getMessage());
        }
    }

    @GetMapping("/flush")
    public ResponseEntity<?> flushRedis() {
        try{
            redisTemplate.getConnectionFactory().getConnection().flushAll();
            return ResponseEntity.ok().body("Redis flushed");
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error flushing redis: " + e.getMessage());
        }

    }


}
