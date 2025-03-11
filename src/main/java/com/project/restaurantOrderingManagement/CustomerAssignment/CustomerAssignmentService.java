package com.project.restaurantOrderingManagement.CustomerAssignment;

import com.project.restaurantOrderingManagement.waitingList.WaitingList;
import java.util.List;

public class CustomerAssignmentService {
    private TableRepository tableRepository = new TableRepository();

    public List<String> assign(WaitingList request) {
        Scheduler scheduler = new Scheduler(tableRepository.fetchTablesFromDB());
        return scheduler.assignTables(request);
    }
}