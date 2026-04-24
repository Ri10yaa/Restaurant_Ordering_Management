package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.exceptions.OperationFailedException;
import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class tableAssignment {

    private static final String WAITER_ASSIGNMENT_KEY = "waiterAssigned:";

    private final tableRepo tableRepo;
    private final waiterAttendance waiterAttendance;
    private final RedisTemplate<String, Object> redisTemplate;

    public tableAssignment(waiterAttendance waiterAttendance,
                           RedisTemplate<String, Object> redisTemplate,
                           tableRepo tableRepo) {
        this.waiterAttendance = waiterAttendance;
        this.redisTemplate = redisTemplate;
        this.tableRepo = tableRepo;
    }

    public Map<String, List<Integer>> assignTable() {
        Map<String, List<Integer>> assignments = new HashMap<>();

        List<table> tables = tableRepo.findAll();
        List<String> activeWaiters = waiterAttendance.getAllActiveWaiters();

        if (activeWaiters.isEmpty()) {
            throw new OperationFailedException("No active waiters found for table assignment");
        }
        if (tables.isEmpty()) {
            throw new OperationFailedException("No tables found for assignment");
        }

        int baseTables = tables.size() / activeWaiters.size();
        int remainingTables = tables.size() % activeWaiters.size();
        int position = 0;

        for (int i = 0; i < activeWaiters.size(); i++) {
            int tablesToAssign = baseTables + (i < remainingTables ? 1 : 0);
            if (tablesToAssign == 0) {
                continue;
            }

            List<Integer> tableRange = tables.subList(position, position + tablesToAssign)
                    .stream()
                    .map(table::getTableNo)
                    .collect(Collectors.toList());

            String waiterCode = activeWaiters.get(i);
            String redisKey = WAITER_ASSIGNMENT_KEY + waiterCode;
            redisTemplate.delete(redisKey);
            redisTemplate.opsForList().rightPushAll(redisKey, tableRange.toArray());

            assignments.put(waiterCode, tableRange);
            position += tablesToAssign;
        }

        return assignments;
    }

    public List<table> getTablesByWaiterCode(String waiterCode) {
        String key = WAITER_ASSIGNMENT_KEY + waiterCode;
        List<Object> tableNos = redisTemplate.opsForList().range(key, 0, -1);

        if (tableNos == null || tableNos.isEmpty()) {
            return new ArrayList<>();
        }

        List<table> tableList = new ArrayList<>();
        for (Object tableNo : tableNos) {
            Optional<table> table = tableRepo.findById(Integer.parseInt(tableNo.toString()));
            table.ifPresent(tableList::add);
        }

        return tableList;
    }
}
