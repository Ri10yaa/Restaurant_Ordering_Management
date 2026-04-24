package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.TableNotFoundException;
import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class tableService {

    private final tableRepo tableRepo;

    public tableService(tableRepo tableRepo) {
        this.tableRepo = tableRepo;
    }

    public List<table> getAllTables() {
        return tableRepo.findAll();
    }

    public table addTable(table table) {
        return tableRepo.save(table);
    }

    public table updateTable(int tableNo, int seats) {
        table existing = tableRepo.findById(tableNo)
                .orElseThrow(() -> new TableNotFoundException("Table " + tableNo + " not found"));

        existing.setNoOfSeats(seats);
        return tableRepo.save(existing);
    }

    public void deleteTable(int tableNo) {
        if (!tableRepo.existsById(tableNo)) {
            throw new TableNotFoundException("Table " + tableNo + " does not exist");
        }
        tableRepo.deleteById(tableNo);
    }
}
