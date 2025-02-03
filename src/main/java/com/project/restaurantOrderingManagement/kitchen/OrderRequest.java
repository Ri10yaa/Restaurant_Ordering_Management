package com.project.restaurantOrderingManagement.kitchen;

public class OrderRequest {
    private String foodCode;
    private String billno;

    // Constructors
    public OrderRequest() {}
    public OrderRequest(String foodCode, String billno) {
        this.foodCode = foodCode;
        this.billno = billno;
    }

    // Getters and Setters
    public String getFoodCode() {
        return foodCode;
    }

    public void setFoodCode(String foodCode) {
        this.foodCode = foodCode;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }
}
