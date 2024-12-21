package com.project.restaurantOrderingManagement.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
@Document(collection = "")
public class Log {
    @Id
    private int billNo;
    private String waiterCode;
    private double amount;
    private ArrayList<foodItem> foodItems;
    private Date date;

    public Log() {}

    public int getBillNo() {
        return billNo;
    }

    public void setBillNo(int billNo) {
        this.billNo = billNo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<foodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(ArrayList<foodItem> foodItems) {
        this.foodItems = foodItems;
    }

    public String getWaiterCode() {
        return waiterCode;
    }

    public void setWaiterCode(String waiterCode) {
        this.waiterCode = waiterCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }



}
