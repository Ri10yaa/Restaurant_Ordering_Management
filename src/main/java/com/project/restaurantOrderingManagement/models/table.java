package com.project.restaurantOrderingManagement.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "")
public class table {
    @Id
    private int tableNo;
    private String waiterCode;
    private int noOfSeats;
    private String status; // using or not using

    public table() {}

    public int getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(int noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTableNo() {
        return tableNo;
    }

    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
    }

    public String getWaiterCode() {
        return waiterCode;
    }

    public void setWaiterCode(String waiterCode) {
        this.waiterCode = waiterCode;
    }
}
