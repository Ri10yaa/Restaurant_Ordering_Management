package com.project.restaurantOrderingManagement.admin;

import com.project.restaurantOrderingManagement.models.Food;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manager/food")
public class foodController {
    private final ManagerService managerService;
    public foodController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/{code}")
    public ResponseEntity<Food> getFood(@PathVariable String code) {
        Food food = managerService.getFoodItem(code);
        return ResponseEntity.ok(food);
    }

    @PostMapping
    public ResponseEntity<Food> addFood(@RequestBody Food food) {
        Food foodItem = managerService.addFoodItem(food);
        return ResponseEntity.ok(foodItem);
    }

    @PutMapping("/{code}")
    public ResponseEntity<Food> updateFood(@PathVariable String code, @RequestBody Food food) {
        Food foodItem = managerService.updateFoodItem(code, food);
        return ResponseEntity.ok(foodItem);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Food> deleteFood(@PathVariable String code) {
        managerService.removeFoodItem(code);
        return ResponseEntity.noContent().build();
    }

}
