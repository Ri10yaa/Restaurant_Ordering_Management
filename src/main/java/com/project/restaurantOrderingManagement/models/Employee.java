package com.project.restaurantOrderingManagement.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "")
public class Employee {
    @Id
 private String empCode;
 private String empName;
 private String empRole;
 private  String spec;

    public Employee() {}

    public String getEmpCode() {
        return empCode;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getSpec(String spec) {
        return this.spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getEmpRole(String role) {
        return empRole;
    }

    public void setEmpRole(String empRole) {
        this.empRole = empRole;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }
}
