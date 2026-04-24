package com.project.restaurantOrderingManagement.CustomerAssignment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantOrderingManagement.service.tableStatusService;
import com.project.restaurantOrderingManagement.waitingList.WaitingList;
import com.project.restaurantOrderingManagement.waitingList.WaitingListService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CustomerAssignmentService {

    private final WaitingListService waitingListService;
    private final tableStatusService statusService;
    private final ObjectMapper objectMapper;

    public CustomerAssignmentService(WaitingListService waitingListService,
                                     tableStatusService statusService,
                                     ObjectMapper objectMapper) {
        this.waitingListService = waitingListService;
        this.statusService = statusService;
        this.objectMapper = objectMapper;
    }

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

        return freeSeats >= seats ? availableTables : Collections.emptyList();
    }

    public void allocateTablesFromWaitingList(String freedTableNo, int freedSeats) {
        List<WaitingList> waitingList = waitingListService.getWaitingList();
        if (waitingList.isEmpty()) {
            return;
        }

        Map<String, Integer> tableStatus = statusService.getTableStatus();

        for (WaitingList customer : new ArrayList<>(waitingList)) {
            int seatsRequired = customer.getSeatsRequired();
            int seatsInFreedTable = tableStatus.getOrDefault(freedTableNo, 0);

            if (seatsRequired <= seatsInFreedTable) {
                statusService.updateTableStatus(freedTableNo, seatsInFreedTable - seatsRequired);
                removeFromWaitingList(customer);
                return;
            }

            if (customer.isOkToSplit()) {
                List<String> splitTables = findFreeTables(seatsRequired);
                if (!splitTables.isEmpty()) {
                    int remainingSeats = seatsRequired;

                    for (String tableNo : splitTables) {
                        int availableSeats = tableStatus.get(tableNo);
                        int seatsToAllocate = Math.min(availableSeats, remainingSeats);
                        statusService.updateTableStatus(tableNo, availableSeats - seatsToAllocate);
                        remainingSeats -= seatsToAllocate;
                        if (remainingSeats == 0) {
                            break;
                        }
                    }

                    removeFromWaitingList(customer);
                    return;
                }
            }
        }
    }

    private void removeFromWaitingList(WaitingList customer) {
        try {
            waitingListService.removeFromWaitingList(objectMapper.writeValueAsString(customer));
        } catch (JsonProcessingException ignored) {
            // best effort removal by serialized value; ignoring if conversion fails
        }
    }
}
