package com.project.restaurantOrderingManagement.admin;

import com.project.restaurantOrderingManagement.models.Employee;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ObjectInputFilter;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

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
    public ResponseEntity<Employee> getEmployee(@PathVariable("code") String code) {
        Optional<Employee> emp = Optional.ofNullable(managerService.getStaff(code));
        if(emp.isPresent()) {
            return ResponseEntity.ok(emp.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String code) {
        managerService.removeStaff(code);
        return ResponseEntity.ok().build();
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
