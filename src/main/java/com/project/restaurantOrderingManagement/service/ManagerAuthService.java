package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.BadRequestException;
import com.project.restaurantOrderingManagement.exceptions.OperationFailedException;
import com.project.restaurantOrderingManagement.models.ManagerCredential;
import com.project.restaurantOrderingManagement.repositories.managerCredentialRepo;
import org.springframework.stereotype.Service;

@Service
public class ManagerAuthService {

    private static final String DEFAULT_ID = "default";
    private final managerCredentialRepo managerCredentialRepo;

    public ManagerAuthService(managerCredentialRepo managerCredentialRepo) {
        this.managerCredentialRepo = managerCredentialRepo;
    }

    public ManagerCredential getOrCreateDefaultCredential() {
        return managerCredentialRepo.findById(DEFAULT_ID).orElseGet(() -> {
            ManagerCredential credential = new ManagerCredential();
            credential.setId(DEFAULT_ID);
            credential.setUsername("manager");
            credential.setPassword("manager@123");
            credential.setMustChangePassword(true);
            return managerCredentialRepo.save(credential);
        });
    }

    public ManagerCredential login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BadRequestException("Manager username and password are required");
        }

        ManagerCredential credential = getOrCreateDefaultCredential();
        if (!credential.getUsername().equals(username) || !credential.getPassword().equals(password)) {
            throw new BadRequestException("Invalid manager credentials");
        }
        return credential;
    }

    public ManagerCredential changeCredentials(String currentUsername,
                                               String currentPassword,
                                               String newUsername,
                                               String newPassword) {
        if (newUsername == null || newUsername.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new BadRequestException("New username and password are required");
        }

        ManagerCredential credential = login(currentUsername, currentPassword);
        credential.setUsername(newUsername);
        credential.setPassword(newPassword);
        credential.setMustChangePassword(false);

        try {
            return managerCredentialRepo.save(credential);
        } catch (Exception e) {
            throw new OperationFailedException("Unable to update manager credentials", e);
        }
    }
}
