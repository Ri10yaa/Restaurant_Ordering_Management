package com.project.restaurantOrderingManagement.admin;

import com.project.restaurantOrderingManagement.exceptions.FoodNotFoundException;
import com.project.restaurantOrderingManagement.models.Food;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/manager/food")
public class foodController {
    private final ManagerService managerService;
    public foodController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/{code}")
    public ResponseEntity<Food> getFood(@PathVariable("code") String code) {
        Food food = managerService.getFoodItem(code).orElseThrow( () -> new FoodNotFoundException("Food with " +code+ " not found"));
        return ResponseEntity.ok(food);
    }

    @PostMapping
    public ResponseEntity<Food> addFood(@RequestBody Food food) {
        Food foodItem = managerService.addFoodItem(food);
        return ResponseEntity.ok(foodItem);
    }

    @PutMapping("/{code}")
    public ResponseEntity<Food> updateFood(@PathVariable("code") String code, @RequestBody Food food) {
        Food foodItem = managerService.updateFoodItem(code, food);
        return ResponseEntity.ok(foodItem);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Food> deleteFood(@PathVariable("code") String code) {
        managerService.removeFoodItem(code);
        return ResponseEntity.noContent().build();
    }

}
