package com.project.restaurantOrderingManagement.exceptions;

public class TableNotFoundException extends RuntimeException {
    public TableNotFoundException(String msg) {
        super(msg);
    }
}
