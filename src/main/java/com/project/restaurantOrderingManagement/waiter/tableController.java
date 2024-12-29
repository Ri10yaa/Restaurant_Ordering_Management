package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.service.tableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tables")
public class tableController {
    @Autowired
    private final tableService service;
    public tableController(tableService service) {
        this.service = service;
    }

    @GetMapping("/{waitercode}")
    public ResponseEntity<List<table>> getAllTables(@PathVariable("waitercode") String waitercode){
        return ResponseEntity.ok(service.getAllTablesByWaiter(waitercode));
    }

}
