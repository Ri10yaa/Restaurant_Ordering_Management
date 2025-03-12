package com.project.restaurantOrderingManagement.CustomerAssignment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantOrderingManagement.waitingList.WaitingList;
import com.project.restaurantOrderingManagement.waitingList.WaitingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerAssignmentService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private WaitingListService waitingListService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String WAITING_LIST_KEY = "waitingList";

    public void assignCustomerToTable(String tableNo) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        List<String> waitingListEntries = listOps.range(WAITING_LIST_KEY, 0, -1);

        if (waitingListEntries == null || waitingListEntries.isEmpty()) {
            System.out.println("No customers in the waiting list.");
            return;
        }

        for (String entryJson : waitingListEntries) {
            try {
                WaitingList customer = objectMapper.readValue(entryJson, WaitingList.class);

                int tableSize = getTableSize(tableNo);

                if (customer.getSeatsRequired() <= tableSize) {
                    // Assign customer to table
                    System.out.println("Assigning " + customer.getName() + " to table " + tableNo);

                    // Remove customer from waiting list
                    waitingListService.removeFromWaitingList(entryJson);
                    return;
                }
            } catch (JsonProcessingException e) {
                System.err.println("Error parsing waiting list entry: " + e.getMessage());
            }
        }

        System.out.println("No suitable customer found for table " + tableNo);
    }

    private int getTableSize(String tableNo) {
        // Example logic for table sizes
        if (tableNo.contains("2")) return 2;
        if (tableNo.contains("4")) return 4;
        if (tableNo.contains("6")) return 6;
        return 4; // Default to 4-seater
    }
}
