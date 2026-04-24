package com.project.restaurantOrderingManagement.repositories;

import com.project.restaurantOrderingManagement.models.ManagerCredential;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface managerCredentialRepo extends MongoRepository<ManagerCredential, String> {
    Optional<ManagerCredential> findByUsername(String username);
}
