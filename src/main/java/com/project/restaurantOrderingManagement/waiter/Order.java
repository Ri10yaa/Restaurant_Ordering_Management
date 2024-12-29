package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.Food;

public class Order {
    String foodCode;
    int quantity;
    String status;

    public Order(String foodCode, int quantity, String status) {
        this.foodCode = foodCode;
        this.quantity = quantity;
        this.status = status;
    }

    public String getFoodCode() {
        return foodCode;
    }

    public void setFoodCode(String foodCode) {
        this.foodCode = foodCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
