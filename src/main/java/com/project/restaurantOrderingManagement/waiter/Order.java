package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.exceptions.BadRequestException;

import java.util.Map;

public class Order {
    private String foodCode;
    private int quantity;
    private String status;
    private String chefCode;
    private String billNo;

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChefCode() {
        return chefCode;
    }

    public void setChefCode(String chefCode) {
        this.chefCode = chefCode;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public static Order mapToOrder(Map<Object, Object> orderMap, String foodCode) {
        return mapToOrder(orderMap, foodCode, null);
    }

    public static Order mapToOrder(Map<Object, Object> orderMap, String foodCode, String billNo) {
        if (orderMap == null || orderMap.isEmpty()) {
            throw new BadRequestException("Order map is empty");
        }

        Order order = new Order();
        order.setFoodCode(foodCode);
        order.setBillNo(billNo);

        Object quantityObj = orderMap.get("quantity");
        Object statusObj = orderMap.get("status");
        Object chefCodeObj = orderMap.get("chefCode");

        if (quantityObj == null || statusObj == null) {
            throw new BadRequestException("Order data is incomplete in Redis");
        }

        order.setQuantity(Integer.parseInt(quantityObj.toString()));
        order.setStatus(statusObj.toString());
        order.setChefCode(chefCodeObj != null ? chefCodeObj.toString() : null);

        return order;
    }
}
