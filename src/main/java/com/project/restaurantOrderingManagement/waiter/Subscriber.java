package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.CustomerAssignment.CustomerAssignmentService;
import com.project.restaurantOrderingManagement.service.tableStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class Subscriber implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(Subscriber.class);

    private final tableStatusService statusService;
    private final CustomerAssignmentService customerAssignmentService;

    public Subscriber(tableStatusService statusService,
                      CustomerAssignmentService customerAssignmentService) {
        this.statusService = statusService;
        this.customerAssignmentService = customerAssignmentService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(pattern);
            String payload = new String(message.getBody());

            if (channel.equals("table-updates")) {
                processTableUpdate(payload);
            } else if (channel.equals("order-updates")) {
                processOrderUpdate(payload);
            } else if (channel.equals("order-deletes")) {
                processOrderDelete(payload);
            }
        } catch (Exception e) {
            log.error("Error processing Redis message", e);
        }
    }

    private void processTableUpdate(String tableNo) {
        int seats = Integer.parseInt(statusService.getStatus(tableNo));
        customerAssignmentService.allocateTablesFromWaitingList(tableNo, seats);
        log.info("Processed table update for table {}", tableNo);
    }

    private void processOrderUpdate(String orderKey) {
        log.info("Order updated: {}", orderKey);
    }

    private void processOrderDelete(String orderKey) {
        log.info("Order deleted: {}", orderKey);
    }
}
