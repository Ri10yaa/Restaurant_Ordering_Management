package com.project.restaurantOrderingManagement.models;

public class foodWithAvailability extends Food{
    private int availability;
    public foodWithAvailability(Food food, int availability) {
        super();
        this.setFoodCode(food.getFoodCode());
        this.setFoodName(food.getFoodName());
        this.setCategory(food.getCategory());
        this.setPrice(food.getPrice());
        this.setVegetarian(food.isVeg());
        this.setMealType(food.getMealType());
        this.availability = availability;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }
}
