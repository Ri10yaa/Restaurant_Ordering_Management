package com.project.restaurantOrderingManagement.Login;

import com.project.restaurantOrderingManagement.models.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/login")
public class loginController {
    @Autowired
    private final loginService loginService;
    public loginController(loginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody loginDTO loginDTO) {
         Employee emp = loginService.authenticate(loginDTO);
         return emp!=null ? ResponseEntity.ok(emp) : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee does not exist!");
    }

}
