package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.Food;

import java.util.Map;

public class Order {
    String foodCode;
    int quantity;
    String status;
    String chefCode;

    public String getChefCode() {
        return chefCode;
    }

    public void setChefCode(String chefCode) {
        this.chefCode = chefCode;
    }

    public Order() {}

    public Order(String foodCode, int quantity, String status) {
        this.foodCode = foodCode;
        this.quantity = quantity;
        this.status = status;
    }
    public Order(String foodCode, int quantity, String status, String chefCode) {
        this.foodCode = foodCode;
        this.quantity = quantity;
        this.status = status;
        this.chefCode = chefCode;
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

    public Order mapToOrder(Map<Object, Object> orderMap) {
        Order order = new Order();
        order.setFoodCode(orderMap.get("foodCode").toString());
        order.setQuantity(Integer.parseInt(orderMap.get("quantity").toString()));
        order.setChefCode(orderMap.get("chefCode").toString());
        return order;
    }
}
