package com.project.restaurantOrderingManagement.models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employee")
public class Manager extends Employee {
    String department;

    public Manager() {
        super();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
