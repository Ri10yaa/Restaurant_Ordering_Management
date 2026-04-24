package com.project.restaurantOrderingManagement.manager;

import com.project.restaurantOrderingManagement.models.RestaurantSettings;
import com.project.restaurantOrderingManagement.service.RestaurantSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/manager/config")
public class ManagerConfigController {

    private final RestaurantSettingsService settingsService;

    public ManagerConfigController(RestaurantSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/hours")
    public ResponseEntity<RestaurantSettings> getHours() {
        return ResponseEntity.ok(settingsService.getOrCreate());
    }

    @PutMapping("/hours")
    public ResponseEntity<RestaurantSettings> updateHours(@RequestBody RestaurantHoursRequest request) {
        return ResponseEntity.ok(settingsService.updateHours(request.getOpeningTime(), request.getClosingTime()));
    }

    @GetMapping("/closing-alert")
    public ResponseEntity<Map<String, Object>> closingAlertStatus() {
        boolean active = settingsService.isClosingAlertActive();
        Map<String, Object> response = new HashMap<>();
        response.put("active", active);
        response.put("message", active
                ? "Closing time reached. Please call resetData (GET /schedule/reset)."
                : "No active closing alert.");
        return ResponseEntity.ok(response);
    }
}
