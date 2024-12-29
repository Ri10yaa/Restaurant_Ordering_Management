package com.project.restaurantOrderingManagement.models;


import com.project.restaurantOrderingManagement.waiter.Order;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "logs")
public class Log {
    @Id
    private long billNo;
    private String waiterCode;
    private double amount;
    private List<Order> foodItems;
    private Date date;
    public Log() {}
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public long getBillNo() {
        return billNo;
    }
    public void setBillNo(long billNo) {
        this.billNo = billNo;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public List<Order> getFoodItems() {
        return foodItems;
    }
    public void setFoodItems(List<Order> foodItems) {
        this.foodItems = foodItems;
    }
    public String getWaiterCode() {
        return waiterCode;
    }
    public void setWaiterCode(String waiterCode) {
        this.waiterCode = waiterCode;
    }

}
