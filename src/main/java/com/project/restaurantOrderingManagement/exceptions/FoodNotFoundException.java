package com.project.restaurantOrderingManagement.exceptions;

public class FoodNotFoundException extends RuntimeException {
    public FoodNotFoundException(String message) {
        super(message);
    }
}
