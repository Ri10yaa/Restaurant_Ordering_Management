package com.project.restaurantOrderingManagement.models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "")
public class foodItem {
    private Food item;
    private int quantity;
}
