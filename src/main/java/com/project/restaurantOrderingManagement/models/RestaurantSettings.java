package com.project.restaurantOrderingManagement.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "restaurantSettings")
public class RestaurantSettings {
    @Id
    private String id;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private LocalDate lastResetDate;
    private LocalDate lastClosingNotificationDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public LocalDate getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(LocalDate lastResetDate) {
        this.lastResetDate = lastResetDate;
    }

    public LocalDate getLastClosingNotificationDate() {
        return lastClosingNotificationDate;
    }

    public void setLastClosingNotificationDate(LocalDate lastClosingNotificationDate) {
        this.lastClosingNotificationDate = lastClosingNotificationDate;
    }
}
