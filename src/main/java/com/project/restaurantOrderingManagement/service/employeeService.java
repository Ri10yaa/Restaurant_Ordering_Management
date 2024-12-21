package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.admin.empInfo;
import com.project.restaurantOrderingManagement.models.Employee;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class employeeService {
    @Autowired
    private final empRepo erepo;

    public employeeService(empRepo erepo) {
        this.erepo = erepo;
    }

    public String formCode(empInfo emp) {
        String code  = "";
        if(emp.getRole().equals("waiter")) {
            code+="W";
        }
        else if(emp.getRole().equals("chef")) {
            code+="C";
        }
        else if(emp.getRole().equals("manger")) {
            code+="M";
        }
        String name = emp.getLastName().replace(" ","").substring(0,2).toUpperCase();
        code += name;
        String dob = emp.getDate().substring(0,4);
        code += dob;
        return code;
    }
    public void addEmployee(empInfo emp) {
        String code = formCode(emp);
        Employee e = new Employee();
        e.setEmpCode(code);
        e.setEmpName(emp.getFirstName());
        e.setEmpRole(emp.getRole());
        e.setSpec(emp.getSpec());
        erepo.save(e);
    }

    public Employee updateEmployee(String code,empInfo emp){
        Optional<Employee> en = erepo.findById(code);
        if(en.isPresent()) {
            Employee e = en.get();
            e.setEmpCode(code);
            e.setEmpName(emp.getFirstName());
            e.setEmpRole(emp.getRole());
            e.setSpec(emp.getSpec());
            return erepo.save(e);
        }
        else{
            return null;
        }
    }

    public void deleteEmployee(String code){
        erepo.deleteById(code);
    }

    public Employee getEmployee(String code){
        return erepo.findById(code).get();
    }

}
