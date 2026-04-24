package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.manager.OnboardingStatusResponse;
import com.project.restaurantOrderingManagement.models.Employee;
import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.models.RestaurantSettings;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import com.project.restaurantOrderingManagement.repositories.foodRepo;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OnboardingStatusService {

    private final ManagerAuthService managerAuthService;
    private final RestaurantSettingsService restaurantSettingsService;
    private final empRepo empRepo;
    private final foodRepo foodRepo;
    private final tableRepo tableRepo;
    private final RedisTemplate<String, Object> redisTemplate;

    public OnboardingStatusService(ManagerAuthService managerAuthService,
                                   RestaurantSettingsService restaurantSettingsService,
                                   empRepo empRepo,
                                   foodRepo foodRepo,
                                   tableRepo tableRepo,
                                   RedisTemplate<String, Object> redisTemplate) {
        this.managerAuthService = managerAuthService;
        this.restaurantSettingsService = restaurantSettingsService;
        this.empRepo = empRepo;
        this.foodRepo = foodRepo;
        this.tableRepo = tableRepo;
        this.redisTemplate = redisTemplate;
    }

    public OnboardingStatusResponse getStatus() {
        List<Employee> employees = empRepo.findAll();
        List<Food> foods = foodRepo.findAll();

        long waiterCount = employees.stream().filter(e -> e.getEmpCode() != null && e.getEmpCode().startsWith("W")).count();
        long chefCount = employees.stream().filter(e -> e.getEmpCode() != null && e.getEmpCode().startsWith("C")).count();
        long managerCount = employees.stream().filter(e -> e.getEmpCode() != null && e.getEmpCode().startsWith("M")).count();
        long foodCount = foods.size();
        long tableCount = tableRepo.count();

        boolean managerCredentialChanged = !managerAuthService.getOrCreateDefaultCredential().isMustChangePassword();

        RestaurantSettings settings = restaurantSettingsService.getOrCreate();
        boolean hoursConfigured = settings.getOpeningTime() != null
                && settings.getClosingTime() != null
                && settings.getOpeningTime().isBefore(settings.getClosingTime());

        boolean foodQtyConfiguredForAllFoods = true;
        for (Food food : foods) {
            if (!Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey("foodAvailability", food.getFoodCode()))) {
                foodQtyConfiguredForAllFoods = false;
                break;
            }
        }

        boolean hasWaiters = waiterCount > 0;
        boolean hasChefs = chefCount > 0;
        boolean hasManagers = managerCount > 0;
        boolean hasFoods = foodCount > 0;
        boolean hasTables = tableCount > 0;

        List<String> pending = new ArrayList<>();
        if (!managerCredentialChanged) pending.add("Change default manager username/password");
        if (!hoursConfigured) pending.add("Set opening and closing hours");
        if (!hasWaiters) pending.add("Add at least one waiter");
        if (!hasChefs) pending.add("Add at least one chef");
        if (!hasManagers) pending.add("Add at least one manager employee record");
        if (!hasFoods) pending.add("Add food menu items");
        if (!hasTables) pending.add("Add dining tables");
        if (hasFoods && !foodQtyConfiguredForAllFoods) pending.add("Set daily quantity for all food items");

        OnboardingStatusResponse response = new OnboardingStatusResponse();
        response.setManagerCredentialChanged(managerCredentialChanged);
        response.setHoursConfigured(hoursConfigured);
        response.setHasWaiters(hasWaiters);
        response.setHasChefs(hasChefs);
        response.setHasManagers(hasManagers);
        response.setHasFoods(hasFoods);
        response.setHasTables(hasTables);
        response.setFoodQuantityConfiguredForAllFoods(foodQtyConfiguredForAllFoods);
        response.setWaiterCount(waiterCount);
        response.setChefCount(chefCount);
        response.setManagerCount(managerCount);
        response.setFoodCount(foodCount);
        response.setTableCount(tableCount);
        response.setPendingItems(pending);
        response.setComplete(pending.isEmpty());

        return response;
    }
}
