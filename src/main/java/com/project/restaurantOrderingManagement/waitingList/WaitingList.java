package com.project.restaurantOrderingManagement.waitingList;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WaitingList {
    private String name;
    private String phoneNumber;
    private int seatsRequired;
    private boolean isOkToSplit; // Added field

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeOfEntry;

    public WaitingList(String name, String phoneNumber, int seatsRequired, boolean isOkToSplit, LocalDateTime timeOfEntry) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.seatsRequired = seatsRequired;
        this.isOkToSplit = isOkToSplit;
        this.timeOfEntry = timeOfEntry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getSeatsRequired() {
        return seatsRequired;
    }

    public void setSeatsRequired(int seatsRequired) {
        this.seatsRequired = seatsRequired;
    }

    public boolean isOkToSplit() {
        return isOkToSplit;
    }

    public void setOkToSplit(boolean okToSplit) {
        isOkToSplit = okToSplit;
    }

    public LocalDateTime getTimeOfEntry() {
        return timeOfEntry;
    }

    public void setTimeOfEntry(LocalDateTime timeOfEntry) {
        this.timeOfEntry = timeOfEntry;
    }
}
