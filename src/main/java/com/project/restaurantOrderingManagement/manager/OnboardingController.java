package com.project.restaurantOrderingManagement.manager;

import com.project.restaurantOrderingManagement.service.OnboardingStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager/onboarding")
public class OnboardingController {

    private final OnboardingStatusService onboardingStatusService;

    public OnboardingController(OnboardingStatusService onboardingStatusService) {
        this.onboardingStatusService = onboardingStatusService;
    }

    @GetMapping("/status")
    public ResponseEntity<OnboardingStatusResponse> getStatus() {
        return ResponseEntity.ok(onboardingStatusService.getStatus());
    }
}
