package com.project.restaurantOrderingManagement.configuration;

import com.project.restaurantOrderingManagement.service.RestaurantSettingsService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ClosingTimeNotifier {

    private final RestaurantSettingsService restaurantSettingsService;
    private final SimpMessagingTemplate messagingTemplate;

    public ClosingTimeNotifier(RestaurantSettingsService restaurantSettingsService,
                               SimpMessagingTemplate messagingTemplate) {
        this.restaurantSettingsService = restaurantSettingsService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(cron = "0 * * * * *")
    public void notifyClosingTime() {
        if (restaurantSettingsService.shouldPublishClosingNotificationNow()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "CLOSING_TIME_ALERT");
            payload.put("message", "Closing time reached. Please call resetData (GET /schedule/reset).");
            messagingTemplate.convertAndSend("/topic/manager-alerts", payload);
            restaurantSettingsService.markClosingNotificationPublishedToday();
        }
    }
}
