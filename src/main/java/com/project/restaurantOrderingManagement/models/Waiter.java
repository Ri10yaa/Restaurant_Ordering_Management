package com.project.restaurantOrderingManagement.models;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "employee")
public class Waiter extends Employee{
    private String employmentType;
    public Waiter() {
        super();
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }
}
