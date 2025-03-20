package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.admin.empInfo;
import com.project.restaurantOrderingManagement.exceptions.EmployeeNotFoundException;
import com.project.restaurantOrderingManagement.exceptions.OrderNotFoundException;
import com.project.restaurantOrderingManagement.models.*;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class employeeService {
    @Autowired
    private final empRepo erepo;

    public employeeService(empRepo erepo) {
        this.erepo = erepo;
    }

    public String formCode(empInfo emp) {
        String code  = "";
        Random ram = new Random();
        int randomNum = ram.nextInt(9000) % 900 ;
        if(emp.getRole().equalsIgnoreCase("waiter")) {
            code+="W";
        }
        else if(emp.getRole().equalsIgnoreCase("chef")) {
            code+="C";
        }
        else if(emp.getRole().equalsIgnoreCase("manager")) {
            code+="M";
        }
        else if(emp.getRole().equalsIgnoreCase("head chef")) {
            code+="HC";
        }
        String name = emp.getLastName().replace(" ","");
        if(name.length() >= 2){
            name = name.substring(0,2).toUpperCase();
        }
        else{
            name = name.toUpperCase();
        }
        code += name;

        String dob = emp.getDate().replace("/","").substring(3,4);
        code += dob;
        code+=String.valueOf(randomNum);
        return code;
    }

    public Employee addEmployee(empInfo emp) {
        String code = formCode(emp);
        String firstName = emp.getFirstName();
        String lastName = emp.getLastName();
        String fullName = firstName + " " + lastName;

        if (emp.getRole().equalsIgnoreCase("waiter")) {
            Waiter waiter = new Waiter();
            waiter.setEmpCode(code);
            waiter.setEmpName(fullName);
            waiter.setPhNo(emp.getPhone());
            waiter.setSalary(emp.getSalary());
            waiter.setEmploymentType(emp.getEmploymentType());
            return erepo.save(waiter);
        } else if (emp.getRole().equalsIgnoreCase("chef")) {
            Chef chef = new Chef();
            chef.setEmpCode(code);
            chef.setEmpName(fullName);
            chef.setPhNo(emp.getPhone());
            chef.setSalary(emp.getSalary());
            chef.setSpec(emp.getSpec() != null && !emp.getSpec().isEmpty() ? emp.getSpec() : null);
            chef.setEmploymentType(emp.getEmploymentType());
            return erepo.save(chef);
        } else if (emp.getRole().equalsIgnoreCase("manager")) {
            Manager manager = new Manager();
            manager.setEmpCode(code);
            manager.setEmpName(fullName);
            manager.setPhNo(emp.getPhone());
            manager.setSalary(emp.getSalary());
            manager.setDepartment(emp.getSpec() != null && !emp.getSpec().isEmpty() ? emp.getSpec().get(0) : null); // Assuming first spec entry is department
            return erepo.save(manager);
        }
        else if (emp.getRole().equalsIgnoreCase("head chef")) {
            Chef chef = new Chef();
            chef.setEmpCode(code);
            chef.setEmpName(fullName);
            chef.setSpec(emp.getSpec() != null && !emp.getSpec().isEmpty() ? emp.getSpec() : null); // Assuming first spec entry is specialization
            return erepo.save(chef);
        }
        else {
            // Handle unknown roles (throw an exception, log an error, etc.)
            throw new IllegalArgumentException("Invalid employee role: " + emp.getRole());
        }
    }

    public Employee updateEmployee(String code, empInfo emp) {
        Employee employee = erepo.findById(code).orElseThrow(() -> new EmployeeNotFoundException("Employee with " + code + "is not found"));

            String firstName = emp.getFirstName();
            String lastName = emp.getLastName();
            String fullName = firstName + " " + lastName;

            if (employee instanceof Waiter) {
                Waiter waiter = (Waiter) employee;
                waiter.setEmpName(fullName);
                waiter.setPhNo(emp.getPhone());
                waiter.setEmploymentType(emp.getEmploymentType());
                waiter.setSalary(emp.getSalary());
                return erepo.save(waiter);
            } else if (employee instanceof Chef) {
                Chef chef = (Chef) employee;
                chef.setEmpName(fullName);
                chef.setSpec(emp.getSpec() != null && !emp.getSpec().isEmpty() ? emp.getSpec() : null);
                chef.setEmploymentType(emp.getEmploymentType());
                chef.setSalary(emp.getSalary());
                chef.setPhNo(emp.getPhone());
                return erepo.save(chef);
            } else if (employee instanceof Manager) {
                Manager manager = (Manager) employee;
                manager.setEmpName(fullName);
                manager.setDepartment(emp.getDept());
                manager.setPhNo(emp.getPhone());
                manager.setSalary(emp.getSalary());
                return erepo.save(manager);
            }
            else {
                // Handle unknown employee types (log an error, throw an exception)
                throw new IllegalStateException("Unknown employee type for code: " + code);
            }
    }



    public void deleteEmployee(String code){
        if(erepo.existsById(code)) {
                erepo.deleteById(code);
        }
        else{
            throw new EmployeeNotFoundException("Employee with code "+code+" does not exist");
        }
    }

    public Optional<Employee> getEmployee(String code){
        return  erepo.findById(code);

    }

}
