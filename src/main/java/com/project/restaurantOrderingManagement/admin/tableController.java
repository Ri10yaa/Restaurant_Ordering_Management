package com.project.restaurantOrderingManagement.admin;

import com.project.restaurantOrderingManagement.exceptions.BadRequestException;
import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import com.project.restaurantOrderingManagement.service.tableStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController("ManagerTableController")
@RequestMapping("/manager/table")
public class tableController {

    private final ManagerService managerService;
    private final tableRepo tableRepo;
    private final tableStatusService tableStatusService;

    public tableController(ManagerService managerService, tableRepo tableRepo, tableStatusService tableStatusService) {
        this.managerService = managerService;
        this.tableRepo = tableRepo;
        this.tableStatusService = tableStatusService;
    }

    @PostMapping
    public ResponseEntity<table> addTable(@RequestBody table table) {
        table last = tableRepo.findTopByOrderByTableNoDesc();
        int nextTableNo = last == null ? 1 : last.getTableNo() + 1;
        table.setTableNo(nextTableNo);
        return ResponseEntity.ok(managerService.addTableItem(table));
    }

    @GetMapping
    public ResponseEntity<List<table>> getTables() {
        return ResponseEntity.ok(managerService.getAllTables());
    }

    @DeleteMapping("/{tableNo}")
    public ResponseEntity<String> deleteTable(@PathVariable int tableNo) {
        managerService.removeTableItem(tableNo);
        return ResponseEntity.ok("Deleted table No: " + tableNo);
    }

    @PutMapping("/{tableNo}")
    public ResponseEntity<table> updateTable(@PathVariable int tableNo, @RequestBody Map<String, Integer> body) {
        Integer seats = body.get("seats");
        if (seats == null || seats <= 0) {
            throw new BadRequestException("seats must be greater than 0");
        }
        return ResponseEntity.ok(managerService.updateTableItem(tableNo, seats));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getLiveTableStatus() {
        return ResponseEntity.ok(tableStatusService.getTableStatus());
    }
}
