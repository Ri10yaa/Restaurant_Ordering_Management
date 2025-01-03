package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.DeleteOperationException;
import com.project.restaurantOrderingManagement.exceptions.EntityNotFoundException;
import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.core.EntityInformation;
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

    public table addTable(table table) {
        return tableRepo.save(table);
    }

    public void deleteTable(int tableNo) {
        Optional<table> existingItem = tableRepo.findById(tableNo);
        if(existingItem.isPresent()) {
            try{
                tableRepo.deleteById(tableNo);
            }
            catch(Exception e) {
                throw new DeleteOperationException("Error while deleting table",e);
            }

        }
        else{
            throw new EntityNotFoundException("Table does not exist");
        }
    }
    //update waiter alone
    public table updateTableByWaiter(int tableNo, String code) {
        table t = tableRepo.findById(tableNo).get();
        System.out.println("COde: " + code);
        t.setWaiterCode(code);
         return tableRepo.save(t);
    }
}
