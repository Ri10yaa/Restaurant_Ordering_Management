package com.project.restaurantOrderingManagement.admin;

import com.project.restaurantOrderingManagement.models.Employee;
import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.service.employeeService;
import com.project.restaurantOrderingManagement.service.foodService;
import com.project.restaurantOrderingManagement.service.tableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {
    @Autowired
    private final foodService foodService;
    @Autowired
    private final employeeService employeeService;
    @Autowired
    private final tableService tableService;

    public ManagerService(employeeService employeeService, foodService foodService, tableService tableService) {
        this.employeeService = employeeService;
        this.foodService = foodService;
        this.tableService = tableService;
    }

    public Food addFoodItem(Food foodItem) {
        return foodService.addFoodItem(foodItem);
    }

    public void removeFoodItem(String code) {
        foodService.deleteFoodItem(code);
    }

    public Food updateFoodItem(String code, Food foodItem) {
       return foodService.updateFoodItem(code, foodItem);
    }

    public Optional<Food> getFoodItem(String code) {
        return foodService.getFoodItem(code);
    }

    public Employee addStaff(empInfo employee) {
        return employeeService.addEmployee(employee);
    }

    public void removeStaff(String code) {
        employeeService.deleteEmployee(code);
    }

    public Employee alterStaff(String code, empInfo employee) {
        return employeeService.updateEmployee(code,employee);
    }

    public Optional<Employee> getStaff(String code) {
        return employeeService.getEmployee(code);
    }

    public table addTableItem(table tableItem) {
        return tableService.addTable(tableItem);
    }

    public List<table> getAllTables(){
        return tableService.getAllTables();
    }

    public table updateTableItem(int no, int seats) {
        return tableService.updateTable(no,seats);
    }

    public void removeTableItem(int tableNo) {
        tableService.deleteTable(tableNo);
    }

}
