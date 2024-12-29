package com.project.restaurantOrderingManagement.admin;

import com.project.restaurantOrderingManagement.models.Employee;
import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.repositories.foodRepo;
import com.project.restaurantOrderingManagement.service.employeeService;
import com.project.restaurantOrderingManagement.service.tableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {
    @Autowired
    private final foodRepo foodRepo;
    @Autowired
    private final employeeService employeeService;
    @Autowired
    private final tableService tableService;

    public ManagerService(employeeService employeeService, foodRepo foodRepo, tableService tableService) {
        this.employeeService = employeeService;
        this.foodRepo = foodRepo;
        this.tableService = tableService;
    }

    public Food addFoodItem(Food foodItem) {
        return foodRepo.save(foodItem);
    }

    public void removeFoodItem(String code) {
        foodRepo.deleteById(code);
    }

    public Food alterMenu(String id, List<String> meals) {
        Optional<Food> existingItem = foodRepo.findById(id);
        if (existingItem.isPresent()) {
            Food item = existingItem.get();
            item.setMealType(meals);
            return foodRepo.save(item);
        }
        throw new RuntimeException("Food item not found with ID: " + id);
    }

    public Employee addStaff(empInfo employee) {
        return employeeService.addEmployee(employee);
    }

    public void removeStaff(String code) {
        employeeService.deleteEmployee(code);
    }

    public Employee alterStaff(String code, Employee employee) {
        return employeeService.updateEmployee(code,employee);
    }

    public Employee getStaff(String code) {
        return employeeService.getEmployee(code);
    }

    public table addTableItem(table tableItem) {
        return tableService.addTable(tableItem);
    }

    public void removeTableItem(int tableNo) {
        tableService.deleteTable(tableNo);
    }

}
