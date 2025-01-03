package com.project.restaurantOrderingManagement.exceptions;

public class DeleteOperationException extends RuntimeException{
    public DeleteOperationException(String message) {
        super(message);
    }
    public DeleteOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
