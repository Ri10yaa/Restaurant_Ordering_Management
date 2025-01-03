package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.admin.empInfo;
import com.project.restaurantOrderingManagement.exceptions.DeleteOperationException;
import com.project.restaurantOrderingManagement.exceptions.EntityNotFoundException;
import com.project.restaurantOrderingManagement.models.Employee;
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
        if(emp.getRole().equals("waiter")) {
            code+="W";
        }
        else if(emp.getRole().equals("chef")) {
            code+="C";
        }
        else if(emp.getRole().equals("manger")) {
            code+="M";
        }
        else if(emp.getRole().equals("head chef")) {
            code+="HC";
        }
        String name = emp.getLastName().replace(" ","").substring(0,2).toUpperCase();
        code += name;

        String dob = emp.getDate().replace("/","").substring(3,4);
        code += dob;
        code+=String.valueOf(randomNum);
        return code;
    }
    public Employee addEmployee(empInfo emp) {
        String code = formCode(emp);
        Employee e = new Employee();
        e.setEmpCode(code);
        e.setEmpName(emp.getFirstName()+ " " + emp.getLastName());
        e.setEmpRole(emp.getRole());
        e.setSpec(emp.getSpec());
        return erepo.save(e);
    }

    public Employee updateEmployee(String code, empInfo emp){
        Optional<Employee> en = erepo.findById(code);
        if(en.isPresent()) {
            Employee e = en.get();
            e.setEmpCode(code);
            e.setEmpName(emp.getFirstName() + " " + emp.getLastName());
            e.setEmpRole(emp.getRole());
            e.setSpec(emp.getSpec());
            return erepo.save(e);
        }
        else{
            return null;
        }
    }

    public void deleteEmployee(String code){
        if(erepo.existsById(code)) {
            try{
                erepo.deleteById(code);
            }
            catch(Exception e) {
                throw new DeleteOperationException("Error while deleting employee with code " +code,e);
            }
        }
        else{
            throw new EntityNotFoundException("Employee with code "+code+" does not exist");
        }
    }

    public Employee getEmployee(String code){
        Optional<Employee> emp = erepo.findById(code);
        if(emp.isPresent()) {
            return emp.get();
        }
        else{
            return null;
        }

    }

}
