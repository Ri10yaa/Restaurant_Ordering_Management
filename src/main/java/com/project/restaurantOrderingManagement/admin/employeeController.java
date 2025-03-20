package com.project.restaurantOrderingManagement.admin;

import com.project.restaurantOrderingManagement.exceptions.EmployeeNotFoundException;
import com.project.restaurantOrderingManagement.models.Employee;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import org.springframework.data.mongodb.core.aggregation.VariableOperators;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ObjectInputFilter;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;
import java.util.Map;
@RestController
@RequestMapping("/manager/employee")
public class employeeController {
    private final ManagerService managerService;
    private final empRepo empRepo;

    public employeeController(ManagerService managerService, empRepo empRepo) {
        this.managerService = managerService;
        this.empRepo = empRepo;
    }

    //for testing
    @GetMapping("/")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(empRepo.findAll());
    }

    @PostMapping
    public ResponseEntity<String> addEmployee(@RequestBody empInfo empInfo) {
        Employee emp = managerService.addStaff(empInfo);
        return ResponseEntity.ok(emp.getEmpCode());
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getEmployee(@PathVariable("code") String code) {
        Employee emp = managerService.getStaff(code).orElseThrow( () -> new EmployeeNotFoundException("Employee " + code + " not found"));
        return ResponseEntity.ok(emp);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("code") String code) {
        managerService.removeStaff(code);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{code}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String code, @RequestBody empInfo employeeInfo) {
       Employee emp = managerService.alterStaff(code, employeeInfo);
       if(emp != null) {
           return ResponseEntity.ok(emp);
       }
       return ResponseEntity.notFound().build();
    }

}
