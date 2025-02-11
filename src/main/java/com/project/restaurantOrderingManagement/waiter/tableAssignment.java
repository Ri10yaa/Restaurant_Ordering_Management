package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.Table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class tableAssignment {
    @Autowired
    tableRepo tableRepo;

    waiterAttendance waiterAttendance;

    private String waiterKey = "waiterAssigned:";

    RedisTemplate<String,String> redisTemplate;

    public tableAssignment(waiterAttendance waiterAttendance, RedisTemplate<String,String> redisTemplate, tableRepo tableRepo) {
        this.waiterAttendance = waiterAttendance;
        this.redisTemplate = redisTemplate;
        this.tableRepo = tableRepo;
    }

    public Map<String, List<Integer>> assignTable() {
        Map<String, List<Integer>> assignments = new HashMap<>();

        try{
            List<Table> tables = tableRepo.findAll();
            List<String> activeWaiters = waiterAttendance.getAllActiveWaiters();

            if (activeWaiters.size() >= 2 && !tables.isEmpty()) {
                int baseTables = tables.size() / activeWaiters.size();
                int remainingTables = tables.size() % activeWaiters.size();
                int pos = 0;

                for (int i = 0; i < activeWaiters.size(); i++) {
                    int tablesToAssign = baseTables + (i < remainingTables ? 1 : 0);
                    List<Integer> tableRange = tables.subList(pos, pos + tablesToAssign)
                            .stream()
                            .map(Table::getTableNo)
                            .collect(Collectors.toList());

                    String waiterCode = activeWaiters.get(i);
                    redisTemplate.delete(waiterKey + waiterCode);
                    redisTemplate.opsForList().rightPushAll(waiterKey + waiterCode, Arrays.toString(tableRange.toArray()));

                    assignments.put(waiterCode, tableRange);

                    pos += tablesToAssign;
                }
            }
            else{
                System.out.println("Waiters not enough to assign table");
            }
        }
        catch (Exception e){
            System.out.println("Error in assignTable" + e.getMessage());
        }
        return assignments;

    }
    //write function to fetch tables when waiter code is given
    List<Table> getTablesByWaiterCode(String waiterCode){
        try{
            String key = "waiterAssigned:" + waiterCode;
            List<String> tables = redisTemplate.opsForList().range(key,0,-1);
            if(tables.isEmpty()|| tables==null){
                throw new RuntimeException("Table not assigned to waiter");
            }
            List<Table> tableList = new ArrayList<>();
            for (String tab : tables) {
                Optional<Table> t = tableRepo.findById(Integer.parseInt(tab));
                tableList.add(t.orElseThrow(() -> new RuntimeException("Table not found: " + tab)));
            }
            return tableList;
        }
        catch (Exception e){
            throw new RuntimeException("Error in getTablesByWaiterCode" + e.getMessage());
        }

    }

}
