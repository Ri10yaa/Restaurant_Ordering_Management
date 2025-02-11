package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class waiterService {
    @Autowired
    tableAssignment tableAssignment;

    public List<Table> fetchTables(String waiterCode) {
        try{
            List<Table> tables = tableAssignment.getTablesByWaiterCode(waiterCode);
            if(tables.isEmpty()){
                return null;
            }
            return tables;
        }
        catch (Exception e){
            throw new RuntimeException("Error fetching tables in service file" + e.getMessage());
        }

    }
}
