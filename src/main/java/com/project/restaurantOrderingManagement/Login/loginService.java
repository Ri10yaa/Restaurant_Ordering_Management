package com.project.restaurantOrderingManagement.Login;

import com.project.restaurantOrderingManagement.models.Employee;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class loginService {
    @Autowired
    private final empRepo empRepo;
    public loginService(empRepo empRepo) {
        this.empRepo = empRepo;
    }

    public Employee authenticate(loginDTO loginDTO) {
        return empRepo.findByEmpCodeAndEmpName(loginDTO.getCode(), loginDTO.getName());
    }
}
