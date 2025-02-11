package com.project.restaurantOrderingManagement.Login;

import com.project.restaurantOrderingManagement.models.Employee;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import com.project.restaurantOrderingManagement.waiter.waiterAttendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class loginService {
    @Autowired
    private final empRepo empRepo;

    private final waiterAttendance waiterAttendance;

    public loginService(empRepo empRepo, com.project.restaurantOrderingManagement.waiter.waiterAttendance waiterAttendance) {
        this.empRepo = empRepo;
        this.waiterAttendance = waiterAttendance;
    }

    public Employee authenticate(loginDTO loginDTO) throws RuntimeException{
        try{
            LocalDateTime now = LocalDateTime.now();
            if(loginDTO.getCode().substring(0, 1).equals("W")) {
                waiterAttendance.makeAttendance(loginDTO.getCode(),now);
            }
            return empRepo.findByEmpCodeAndEmpName(loginDTO.getCode(), loginDTO.getName());
        }
        catch(Exception e){
            throw new RuntimeException("Login Failed");
        }

    }

    public String logout(loginDTO loginDTO) throws RuntimeException{
        try{
            LocalDateTime now = LocalDateTime.now();
            if(loginDTO.getCode().substring(0, 1).equals("W")) {
                waiterAttendance.terminateAttendance(loginDTO.getCode(),now);
            }
            return "Logged out successfully";
        }
        catch(Exception e){
            throw new RuntimeException("Logout Failed");
        }

    }
}
