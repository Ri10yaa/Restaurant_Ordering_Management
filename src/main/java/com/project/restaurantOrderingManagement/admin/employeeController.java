package com.project.restaurantOrderingManagement.admin;

import com.project.restaurantOrderingManagement.models.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/employee")
public class employeeController {
    private final ManagerService managerService;
    public employeeController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @PostMapping("/add")
    public ResponseEntity<Employee> addEmployee(@RequestBody empInfo empInfo) {
        Employee emp = managerService.addStaff(empInfo);
        return ResponseEntity.ok(emp);
    }

    @GetMapping("/get/{code}")
    public ResponseEntity<Employee> getEmployee(@PathVariable String code) {
        Optional<Employee> emp = Optional.ofNullable(managerService.getStaff(code));
        if(emp.isPresent()) {
            return ResponseEntity.ok(emp.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String code) {
        managerService.removeStaff(code);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{code}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String code, @RequestBody Employee empInfo) {
       Employee emp = managerService.alterStaff(code, empInfo);
       if(emp != null) {
           return ResponseEntity.ok(emp);
       }
       return ResponseEntity.notFound().build();
    }

}
