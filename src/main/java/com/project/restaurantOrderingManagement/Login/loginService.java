package com.project.restaurantOrderingManagement.Login;

import com.project.restaurantOrderingManagement.exceptions.BadRequestException;
import com.project.restaurantOrderingManagement.exceptions.EmployeeNotFoundException;
import com.project.restaurantOrderingManagement.models.Employee;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import com.project.restaurantOrderingManagement.service.ChefAttendanceService;
import com.project.restaurantOrderingManagement.waiter.waiterAttendance;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class loginService {

    private final empRepo empRepo;
    private final waiterAttendance waiterAttendance;
    private final ChefAttendanceService chefAttendanceService;

    public loginService(empRepo empRepo, waiterAttendance waiterAttendance, ChefAttendanceService chefAttendanceService) {
        this.empRepo = empRepo;
        this.waiterAttendance = waiterAttendance;
        this.chefAttendanceService = chefAttendanceService;
    }

    public Employee authenticate(loginDTO loginDTO) {
        validateLogin(loginDTO);

        Employee employee = empRepo.findByEmpCodeAndEmpName(loginDTO.getCode(), loginDTO.getName());
        if (employee == null) {
            throw new EmployeeNotFoundException("Employee does not exist for provided credentials");
        }

        if (loginDTO.getCode().startsWith("W")) {
            waiterAttendance.makeAttendance(loginDTO.getCode(), LocalDateTime.now());
        } else if (loginDTO.getCode().startsWith("C")) {
            chefAttendanceService.markLogin(loginDTO.getCode(), LocalDateTime.now());
        }

        return employee;
    }

    public String logout(loginDTO loginDTO) {
        validateLogin(loginDTO);

        if (loginDTO.getCode().startsWith("W")) {
            waiterAttendance.terminateAttendance(loginDTO.getCode(), LocalDateTime.now());
        } else if (loginDTO.getCode().startsWith("C")) {
            chefAttendanceService.markLogout(loginDTO.getCode(), LocalDateTime.now());
        }

        return "Logged out successfully";
    }

    private void validateLogin(loginDTO loginDTO) {
        if (loginDTO == null) {
            throw new BadRequestException("Login payload is required");
        }
        if (loginDTO.getCode() == null || loginDTO.getCode().isBlank()) {
            throw new BadRequestException("Employee code is required");
        }
        if (loginDTO.getName() == null || loginDTO.getName().isBlank()) {
            throw new BadRequestException("Employee name is required");
        }
    }
}
