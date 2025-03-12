package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.DeleteOperationException;
import com.project.restaurantOrderingManagement.exceptions.EntityNotFoundException;
import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class tableService {
    @Autowired
    tableRepo tableRepo;
    @Autowired
    RedisTemplate redisTemplate;

    public List<table> getAllTables() {
       try{
           List<table> tables = tableRepo.findAll();
           return tables;
       } catch (Exception e) {
           throw new RuntimeException("Error in fetching all tables " + e.getMessage());
       }

    }

    public table addTable(table table) {
        return tableRepo.save(table);
    }

    public table updateTable(int tableNo, int seats) {
        try{
            Optional<table> t = tableRepo.findById(tableNo);
            if(t.isPresent()) {
                table t1 = t.get();
                t1.setNoOfSeats(seats);
                tableRepo.save(t1);
                return tableRepo.save(t1);
            }
        }catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("table not found : " + e.getMessage());
        }
        return null;
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
            throw new EntityNotFoundException("table does not exist");
        }
    }

}
