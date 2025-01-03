package com.project.restaurantOrderingManagement.admin;

import com.project.restaurantOrderingManagement.helpers.tableNoIncrementingService;
import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import com.project.restaurantOrderingManagement.service.tableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ManagerTableController")
@RequestMapping("/manager/table")
public class tableController {
    @Autowired
    ManagerService managerService;
    @Autowired
    tableNoIncrementingService service;
    public tableController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @PostMapping
    ResponseEntity<table> addTable(@RequestBody table table) {
        table.setTableNo((Integer) service.incrementTableNo());
        table t = managerService.addTableItem(table);
        return ResponseEntity.ok(t);
    }

    @GetMapping("/{code}")
    ResponseEntity<List<table>> getTables(@PathVariable String code) {
        List<table> tables = managerService.getAllTables(code);
        return ResponseEntity.ok(tables);
    }

    @DeleteMapping("/{tableNo}")
    ResponseEntity<table> deleteTable(@PathVariable String tableNo) {
        int tableNoInt = Integer.parseInt(tableNo);
        managerService.removeTableItem(tableNoInt);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{tableNo}")
    ResponseEntity<table> updateTable(@PathVariable String tableNo, @RequestBody String code) {
        int tableNoInt = Integer.parseInt(tableNo);
        table t = managerService.updateTableItem(tableNoInt,code);
        return ResponseEntity.ok(t);

    }


}
