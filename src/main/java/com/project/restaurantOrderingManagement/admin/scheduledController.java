package com.project.restaurantOrderingManagement.admin;

import com.project.restaurantOrderingManagement.helpers.BillNoIncrementingService;
import com.project.restaurantOrderingManagement.service.foodAvailabilityService;
import com.project.restaurantOrderingManagement.service.RestaurantSettingsService;
import com.project.restaurantOrderingManagement.service.tableStatusService;
import com.project.restaurantOrderingManagement.waiter.tableAssignment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
public class scheduledController {

    private final tableAssignment tableAssignment;
    private final foodAvailabilityService foodAvailabilityService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BillNoIncrementingService billNoIncrementingService;
    private final tableStatusService tableStatusService;
    private final RestaurantSettingsService restaurantSettingsService;

    public scheduledController(tableAssignment tableAssignment,
                               foodAvailabilityService foodAvailabilityService,
                               RedisTemplate<String, Object> redisTemplate,
                               BillNoIncrementingService billNoIncrementingService,
                               tableStatusService tableStatusService,
                               RestaurantSettingsService restaurantSettingsService) {
        this.tableAssignment = tableAssignment;
        this.foodAvailabilityService = foodAvailabilityService;
        this.redisTemplate = redisTemplate;
        this.billNoIncrementingService = billNoIncrementingService;
        this.tableStatusService = tableStatusService;
        this.restaurantSettingsService = restaurantSettingsService;
    }

    @GetMapping("/assignTable")
    public ResponseEntity<Map<String, List<Integer>>> assignTable() {
        return ResponseEntity.ok(tableAssignment.assignTable());
    }

    @GetMapping("/reset")
    public ResponseEntity<String> resetData() {
        foodAvailabilityService.resetAvailability();
        billNoIncrementingService.setBillNo();
        tableStatusService.resetTableStatus();
        restaurantSettingsService.markResetDoneToday();
        return ResponseEntity.ok("Reset completed successfully");
    }

    @GetMapping("/flush")
    public ResponseEntity<String> flushRedis() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        return ResponseEntity.ok("Redis flushed");
    }
}
