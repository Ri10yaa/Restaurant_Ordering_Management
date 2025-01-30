package com.project.restaurantOrderingManagement.admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class empInfo {
    private String firstName;
    private String lastName;
    private String date;
    private String role;
    private List<String> spec;
    public empInfo() {}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getSpec() {
        return spec;
    }

    public void setSpec(List<String> spec) {
        this.spec = spec;
    }
}
