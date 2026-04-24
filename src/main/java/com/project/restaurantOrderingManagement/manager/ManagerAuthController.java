package com.project.restaurantOrderingManagement.manager;

import com.project.restaurantOrderingManagement.models.ManagerCredential;
import com.project.restaurantOrderingManagement.service.ManagerAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/manager/auth")
public class ManagerAuthController {

    private final ManagerAuthService managerAuthService;

    public ManagerAuthController(ManagerAuthService managerAuthService) {
        this.managerAuthService = managerAuthService;
    }

    @GetMapping("/bootstrap")
    public ResponseEntity<Map<String, Object>> bootstrapStatus() {
        ManagerCredential credential = managerAuthService.getOrCreateDefaultCredential();
        Map<String, Object> response = new HashMap<>();
        response.put("username", credential.getUsername());
        response.put("mustChangePassword", credential.isMustChangePassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody ManagerLoginRequest request) {
        ManagerCredential credential = managerAuthService.login(request.getUsername(), request.getPassword());
        Map<String, Object> response = new HashMap<>();
        response.put("username", credential.getUsername());
        response.put("mustChangePassword", credential.isMustChangePassword());
        response.put("message", "Manager login successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-credentials")
    public ResponseEntity<Map<String, Object>> changeCredentials(@RequestBody ManagerCredentialChangeRequest request) {
        ManagerCredential credential = managerAuthService.changeCredentials(
                request.getCurrentUsername(),
                request.getCurrentPassword(),
                request.getNewUsername(),
                request.getNewPassword()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("username", credential.getUsername());
        response.put("mustChangePassword", credential.isMustChangePassword());
        response.put("message", "Manager credentials updated");
        return ResponseEntity.ok(response);
    }
}
