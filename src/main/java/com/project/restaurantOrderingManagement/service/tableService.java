package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.DeleteOperationException;
import com.project.restaurantOrderingManagement.exceptions.EntityNotFoundException;
import com.project.restaurantOrderingManagement.models.Table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.stereotype.Service;
import com.project.restaurantOrderingManagement.models.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class tableService {
    @Autowired
    tableRepo tableRepo;
    @Autowired
    RedisTemplate redisTemplate;

    public List<Table> getAllTables() {
       try{
           List<Table> tables = tableRepo.findAll();
           return tables;
       } catch (Exception e) {
           throw new RuntimeException("Error in fetching all tables " + e.getMessage());
       }

    }

    public Table addTable(Table table) {
        return tableRepo.save(table);
    }

    public Table updateTable(int tableNo,int seats) {
        try{
            Optional<Table> t = tableRepo.findById(tableNo);
            if(t.isPresent()) {
                Table t1 = t.get();
                t1.setNoOfSeats(seats);
                tableRepo.save(t1);
                return tableRepo.save(t1);
            }
        }catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Table not found : " + e.getMessage());
        }
        return null;
    }

    public void deleteTable(int tableNo) {
        Optional<Table> existingItem = tableRepo.findById(tableNo);
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

}
