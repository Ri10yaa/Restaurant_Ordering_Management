package com.project.restaurantOrderingManagement.admin;

import ch.qos.logback.core.status.Status;
import com.project.restaurantOrderingManagement.waiter.foodAvailabilityService;
import com.project.restaurantOrderingManagement.waiter.tableAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
public class scheduledController {
    @Autowired
    private final tableAssignment tableAssignment;
    @Autowired
    private final foodAvailabilityService foodAvailabilityService;

    public scheduledController(com.project.restaurantOrderingManagement.waiter.tableAssignment tableAssignment, com.project.restaurantOrderingManagement.waiter.foodAvailabilityService foodAvailabilityService) {
        this.tableAssignment = tableAssignment;
        this.foodAvailabilityService = foodAvailabilityService;
    }

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

    @GetMapping("/reset")
    public ResponseEntity<?> resetFoodAvailability() {
        try{
            foodAvailabilityService.resetAvailability();
            return ResponseEntity.ok().body("Food Availability reset");
        }
        catch (Exception e){
            return ResponseEntity.status(500).body("Error resetting food availability: " + e.getMessage());
        }
    }


}
