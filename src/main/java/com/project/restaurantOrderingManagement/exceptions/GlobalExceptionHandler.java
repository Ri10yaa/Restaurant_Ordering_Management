package com.project.restaurantOrderingManagement.exceptions;

import com.project.restaurantOrderingManagement.models.Food;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.project.restaurantOrderingManagement.exceptions.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FoodNotFoundException.class)
    public ResponseEntity<?> handleFoodNotFoundException(FoodNotFoundException e) {
        ErrorResponse res = new ErrorResponse("Food not found",e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TableNotFoundException.class)
    public ResponseEntity<?> handleTableNotFoundException(TableNotFoundException e) {
        ErrorResponse res = new ErrorResponse("Table not found",e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<?> handleEmployeeNotFoundException(EmployeeNotFoundException e) {
        ErrorResponse res = new ErrorResponse("Employee not found",e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<?> handleOrderNotFoundException(OrderNotFoundException e) {
        ErrorResponse res = new ErrorResponse("Order not found",e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BillNotFoundException.class)
    public ResponseEntity<?> handleBillNotFoundException(BillNotFoundException e) {
        ErrorResponse res = new ErrorResponse("Bill not found",e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }
}
