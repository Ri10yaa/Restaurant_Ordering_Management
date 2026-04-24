package com.project.restaurantOrderingManagement.manager;

import java.util.List;

public class ChefDisplayDTO {
    private String empCode;
    private String empName;
    private List<String> spec;
    private boolean loggedIn;
    private List<KitchenOrderDTO> orders;

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public List<String> getSpec() {
        return spec;
    }

    public void setSpec(List<String> spec) {
        this.spec = spec;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public List<KitchenOrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<KitchenOrderDTO> orders) {
        this.orders = orders;
    }
}
