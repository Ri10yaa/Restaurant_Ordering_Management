package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.BadRequestException;
import com.project.restaurantOrderingManagement.exceptions.OperationFailedException;
import com.project.restaurantOrderingManagement.exceptions.TableNotFoundException;
import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import com.project.restaurantOrderingManagement.waiter.tablePublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class tableStatusService {

    private static final String KEY = "tableStatus";

    private final RedisTemplate<String, Object> redisTemplate;
    private final tableRepo tableRepo;
    private final tablePublisher tablePublisher;

    public tableStatusService(RedisTemplate<String, Object> redisTemplate,
                              tableRepo tableRepo,
                              tablePublisher tablePublisher) {
        this.redisTemplate = redisTemplate;
        this.tableRepo = tableRepo;
        this.tablePublisher = tablePublisher;
    }

    public void resetTableStatus() {
        List<table> tables = tableRepo.findAll();
        for (table table : tables) {
            redisTemplate.opsForHash().put(KEY, String.valueOf(table.getTableNo()), String.valueOf(table.getNoOfSeats()));
        }
    }

    public void markEngaged(int tableNo, int seatsEngaged) {
        if (seatsEngaged <= 0) {
            throw new BadRequestException("seatsEngaged must be greater than 0");
        }

        Integer seatsAvailable = getSeatAvailability(tableNo);
        if (seatsAvailable < seatsEngaged) {
            throw new BadRequestException("Not enough seats available for table " + tableNo);
        }

        redisTemplate.opsForHash().put(KEY, String.valueOf(tableNo), String.valueOf(seatsAvailable - seatsEngaged));
        tablePublisher.publishTableUpdate(String.valueOf(tableNo));
    }

    public void markVacant(int tableNo, int seatsFreed) {
        if (seatsFreed <= 0) {
            throw new BadRequestException("seatsFreed must be greater than 0");
        }

        Integer seatsAvailable = getSeatAvailability(tableNo);
        redisTemplate.opsForHash().put(KEY, String.valueOf(tableNo), String.valueOf(seatsAvailable + seatsFreed));
        tablePublisher.publishTableUpdate(String.valueOf(tableNo));
    }

    public String getStatus(String tableNo) {
        if (!tableRepo.existsById(Integer.parseInt(tableNo))) {
            throw new TableNotFoundException("Table " + tableNo + " not found");
        }

        Object seats = redisTemplate.opsForHash().get(KEY, tableNo);
        if (seats == null) {
            throw new TableNotFoundException("Table status not initialized for table " + tableNo);
        }

        return seats.toString();
    }

    public Map<String, Integer> getTableStatus() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(KEY);
        Map<String, Integer> tableStatus = new HashMap<>();

        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            tableStatus.put(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString()));
        }

        return tableStatus;
    }

    public void updateTableStatus(String tableNo, int noOfFreeSeats) {
        if (noOfFreeSeats < 0) {
            throw new BadRequestException("noOfFreeSeats cannot be negative");
        }
        redisTemplate.opsForHash().put(KEY, tableNo, String.valueOf(noOfFreeSeats));
        tablePublisher.publishTableUpdate(tableNo);
    }

    private Integer getSeatAvailability(int tableNo) {
        if (!tableRepo.existsById(tableNo)) {
            throw new TableNotFoundException("Table " + tableNo + " not found");
        }

        Object seatsObj = redisTemplate.opsForHash().get(KEY, String.valueOf(tableNo));
        if (seatsObj == null) {
            throw new OperationFailedException("Table status missing for table " + tableNo);
        }

        return Integer.parseInt(seatsObj.toString());
    }
}
