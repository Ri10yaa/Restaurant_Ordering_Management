package com.project.restaurantOrderingManagement.models;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "employee")
public class Chef extends Employee{
    List<String> spec;
    private String employmentType;
    public Chef() {
        super();
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public List<String> getSpec() {
        return spec;
    }

    public void setSpec(List<String> spec) {
        this.spec = spec;
    }
}
