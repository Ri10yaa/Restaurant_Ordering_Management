package com.project.restaurantOrderingManagement.admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class empInfo {
    private String firstName;
    private String lastName;
    private String date;
    private String role;
    private List<String> spec; // for chef
    private String phone;
    private String employmentType;
    private double salary;
    private String dept; //for manager

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public empInfo() {}

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getDate() {
        return date;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
