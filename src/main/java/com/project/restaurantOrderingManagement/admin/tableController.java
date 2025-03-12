package com.project.restaurantOrderingManagement.admin;

import com.project.restaurantOrderingManagement.models.table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ManagerTableController")
@RequestMapping("/manager/table")
public class tableController {
    @Autowired
    ManagerService managerService;
    @Autowired
    private com.project.restaurantOrderingManagement.repositories.tableRepo tableRepo;

    public tableController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @PostMapping
    ResponseEntity<table> addTable(@RequestBody table table) {
        table.setTableNo(tableRepo.findTopByOrderByTableNoDesc().getTableNo() + 1);
        com.project.restaurantOrderingManagement.models.table t = managerService.addTableItem(table);
        return ResponseEntity.ok(t);
    }

    @GetMapping
    ResponseEntity<List<table>> getTables() {
        List<table> tables = managerService.getAllTables();
        return ResponseEntity.ok(tables);
    }

    @DeleteMapping("/{tableNo}")
    ResponseEntity<?> deleteTable(@PathVariable String tableNo) {
        int tableNoInt = Integer.parseInt(tableNo);
        managerService.removeTableItem(tableNoInt);
        return ResponseEntity.ok("Deleted table No : " + tableNo);
    }

    @PutMapping("/{tableNo}")
    ResponseEntity<table> updateTable(@PathVariable String tableNo, @RequestBody int seats) {
        int tableNoInt = Integer.parseInt(tableNo);
        table t = managerService.updateTableItem(tableNoInt,seats);
        return ResponseEntity.ok(t);

    }


}
