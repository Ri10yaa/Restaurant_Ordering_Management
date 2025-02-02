package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class tableAssignment {
    tableRepo tableRepo;
    waiterAttendance waiterAttendance;
    private String waiterKey = "waiter:";
    RedisTemplate redisTemplate;

    public tableAssignment(waiterAttendance waiterAttendance, RedisTemplate redisTemplate, tableRepo tableRepo) {
        this.waiterAttendance = waiterAttendance;
        this.redisTemplate = redisTemplate;
        this.tableRepo = tableRepo;
        this.waiterKey = waiterKey;
    }

    public Map<String, List<Integer>> assignTable() {
        System.out.println("Entered assignTable");
        List<table> tables = tableRepo.findAll();
        List<String> activeWaiters = waiterAttendance.getAllActiveWaiters();

        Map<String, List<Integer>> assignments = new HashMap<>();

        if (activeWaiters.size() > 2 && !tables.isEmpty()) {
            int baseTables = tables.size() / activeWaiters.size();
            int remainingTables = tables.size() % activeWaiters.size();
            int pos = 0;

            for (int i = 0; i < activeWaiters.size(); i++) {
                int tablesToAssign = baseTables + (i < remainingTables ? 1 : 0);

                List<Integer> tableRange = tables.subList(pos, pos + tablesToAssign)
                        .stream()
                        .map(table::getTableNo)  // Assuming there's a getTableNumber method
                        .collect(Collectors.toList());

                String waiterCode = activeWaiters.get(i);
                redisTemplate.opsForValue().set(waiterKey + waiterCode, tableRange);

                // Store in map for return value
                assignments.put(waiterCode, tableRange);

                // Update position
                pos += tablesToAssign;
            }
        }
        else{
            System.out.println("Waiters not enough to assign table");
        }
        return assignments;
    }

}
