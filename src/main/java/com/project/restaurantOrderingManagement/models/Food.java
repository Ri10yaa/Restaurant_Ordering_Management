package com.project.restaurantOrderingManagement.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "food")
public class Food {
    @Id
    private String foodCode;
    private String foodName;
    private String category;
    private List<String> mealType;
    private double price;
    public Food() {}

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFoodCode() {
        return foodCode;
    }
    public void setFoodCode(String foodCode) {
        this.foodCode = foodCode;
    }
    public String getFoodName() {
        return foodName;
    }
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public List<String> getMealType() {
        return mealType;
    }

    public void setMealType(List<String> mealType) {
        this.mealType = mealType;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

}
