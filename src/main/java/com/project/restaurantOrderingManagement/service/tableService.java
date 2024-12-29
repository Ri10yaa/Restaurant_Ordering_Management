package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class tableService {
    @Autowired
    tableRepo tableRepo;
    //get by waiter
    public List<table> getAllTablesByWaiter(String code) {
        return tableRepo.findAllByWaiterCode(code);
    }
    //get by status
    public List<table> getAllTablesByStatus(String status) {
        return tableRepo.findAllByStatus(status);
    }

    public table addTable(table table) {
        return tableRepo.save(table);
    }

    public void deleteTable(int tableNo) {
        Optional<table> existingItem = tableRepo.findById(tableNo);
        if(existingItem.isPresent()) {
            tableRepo.deleteById(tableNo);
        }
        else{
            throw new RuntimeException("Table not found with ID: " + tableNo);
        }
    }
    //update waiter alone
    public table updateTableByWaiter(int tableNo, table table) {
        table t = tableRepo.findById(tableNo).get();
        t.setWaiterCode(table.getWaiterCode());
         return tableRepo.save(t);
    }
}
