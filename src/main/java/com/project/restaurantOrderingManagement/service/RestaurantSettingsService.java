package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.BadRequestException;
import com.project.restaurantOrderingManagement.models.RestaurantSettings;
import com.project.restaurantOrderingManagement.repositories.restaurantSettingsRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class RestaurantSettingsService {

    private static final String DEFAULT_ID = "default";
    private final restaurantSettingsRepo settingsRepo;

    public RestaurantSettingsService(restaurantSettingsRepo settingsRepo) {
        this.settingsRepo = settingsRepo;
    }

    public RestaurantSettings getOrCreate() {
        return settingsRepo.findById(DEFAULT_ID).orElseGet(() -> {
            RestaurantSettings settings = new RestaurantSettings();
            settings.setId(DEFAULT_ID);
            settings.setOpeningTime(LocalTime.of(9, 0));
            settings.setClosingTime(LocalTime.of(23, 0));
            settings.setLastResetDate(null);
            settings.setLastClosingNotificationDate(null);
            return settingsRepo.save(settings);
        });
    }

    public RestaurantSettings updateHours(String opening, String closing) {
        if (opening == null || closing == null || opening.isBlank() || closing.isBlank()) {
            throw new BadRequestException("Opening and closing time are required");
        }

        LocalTime openingTime = LocalTime.parse(opening);
        LocalTime closingTime = LocalTime.parse(closing);
        if (!openingTime.isBefore(closingTime)) {
            throw new BadRequestException("Opening time must be before closing time");
        }

        RestaurantSettings settings = getOrCreate();
        settings.setOpeningTime(openingTime);
        settings.setClosingTime(closingTime);
        return settingsRepo.save(settings);
    }

    public boolean shouldNotifyClosing(LocalDate today, LocalTime now) {
        RestaurantSettings settings = getOrCreate();
        return (now.equals(settings.getClosingTime()) || now.isAfter(settings.getClosingTime()))
                && (settings.getLastResetDate() == null || !today.equals(settings.getLastResetDate()));
    }

    public boolean isClosingAlertActive() {
        RestaurantSettings settings = getOrCreate();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        return (now.equals(settings.getClosingTime()) || now.isAfter(settings.getClosingTime()))
                && (settings.getLastResetDate() == null || !today.equals(settings.getLastResetDate()));
    }

    public void markResetDoneToday() {
        RestaurantSettings settings = getOrCreate();
        settings.setLastResetDate(LocalDate.now());
        settingsRepo.save(settings);
    }

    public boolean shouldPublishClosingNotificationNow() {
        RestaurantSettings settings = getOrCreate();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        boolean closingReached = now.equals(settings.getClosingTime()) || now.isAfter(settings.getClosingTime());
        boolean alreadyPublished = today.equals(settings.getLastClosingNotificationDate());
        boolean resetDone = today.equals(settings.getLastResetDate());

        return closingReached && !alreadyPublished && !resetDone;
    }

    public void markClosingNotificationPublishedToday() {
        RestaurantSettings settings = getOrCreate();
        settings.setLastClosingNotificationDate(LocalDate.now());
        settingsRepo.save(settings);
    }
}
