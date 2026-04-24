package com.project.restaurantOrderingManagement.Login;

import com.project.restaurantOrderingManagement.models.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class loginController {

    private final loginService loginService;

    public loginController(loginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<Employee> login(@RequestBody loginDTO loginDTO) {
        return ResponseEntity.ok(loginService.authenticate(loginDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody loginDTO loginDTO) {
        return ResponseEntity.ok(loginService.logout(loginDTO));
    }
}
