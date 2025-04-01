package com.project.restaurantOrderingManagement.CustomerAssignment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import com.project.restaurantOrderingManagement.service.tableStatusService;
import com.project.restaurantOrderingManagement.waitingList.WaitingList;
import com.project.restaurantOrderingManagement.waitingList.WaitingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerAssignmentService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private tableRepo  tableRepo;
    @Autowired
    private WaitingListService waitingListService;

    @Autowired
    private tableStatusService statusService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String WAITING_LIST_KEY = "waitingList";
    private static final String TABLE_STATUS_KEY = "tableStatus";

    private List<String> findFreeTables(int seats) {
        Map<String, Integer> tableStatus = statusService.getTableStatus();
        List<String> availableTables = new ArrayList<>();
        int freeSeats = 0;

        for (Map.Entry<String, Integer> entry : tableStatus.entrySet()) {
            if (entry.getValue() > 0 && freeSeats < seats) {
                availableTables.add(entry.getKey());
                freeSeats += entry.getValue();
            }
        }

        if (freeSeats >= seats) {
            System.out.println("Found split tables: " + availableTables);
            return availableTables;
        }

        return Collections.emptyList();
    }

    // Allocate tables for waiting list customers
    public void allocateTablesFromWaitingList(String freedTableNo, int freedSeats) {
        List<WaitingList> waitingList = waitingListService.getWaitingList();
        Map<String, Integer> tableStatus = statusService.getTableStatus();
        
        if (waitingList.isEmpty()) {
            System.out.println("Waiting list is empty. No customers to allocate.");
            return;
        }

        System.out.println("Processing waiting list for table allocation...");
        for (WaitingList customer : new ArrayList<>(waitingList)) {
            int seatsRequired = customer.getSeatsRequired();

            if (seatsRequired <= tableStatus.getOrDefault(freedTableNo, 0)) {
                System.out.println("Allocating table " + freedTableNo + " to " + customer.getName());
                statusService.updateTableStatus(freedTableNo, tableStatus.get(freedTableNo) - seatsRequired);
                try {
                    waitingListService.removeFromWaitingList(objectMapper.writeValueAsString(customer));
                } catch (JsonProcessingException e) {
                    System.err.println("Error removing from waiting list: " + e.getMessage());
                }
                return;
            } else if (customer.isOkToSplit()) {
                List<String> splitTables = findFreeTables(seatsRequired);
                if (!splitTables.isEmpty()) {
                    System.out.println("Allocating split tables " + splitTables + " to " + customer.getName());
                    int remainingSeats = seatsRequired;

                    for (String table : splitTables) {
                        int availableSeats = tableStatus.get(table);
                        int seatsToAllocate = Math.min(availableSeats, remainingSeats);
                        statusService.updateTableStatus(table, availableSeats - seatsToAllocate);
                        remainingSeats -= seatsToAllocate;
                        if (remainingSeats == 0) break;
                    }

                    try {
                        waitingListService.removeFromWaitingList(objectMapper.writeValueAsString(customer));
                    } catch (JsonProcessingException e) {
                        System.err.println("Error removing from waiting list: " + e.getMessage());
                    }
                    return;
                } else {
                    System.out.println("No suitable split tables found for " + customer.getName());
                }
            } else {
                System.out.println("Customer " + customer.getName() + " cannot be accommodated.");
            }
        }
    }

}
