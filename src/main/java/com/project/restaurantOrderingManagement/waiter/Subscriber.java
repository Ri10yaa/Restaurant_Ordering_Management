package com.project.restaurantOrderingManagement.waiter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantOrderingManagement.CustomerAssignment.CustomerAssignmentService;
import com.project.restaurantOrderingManagement.service.tableStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.restaurantOrderingManagement.waitingList.WaitingList;
import org.springframework.data.redis.core.ListOperations;


import java.util.List;
import java.util.Objects;

@Component
public class Subscriber implements MessageListener {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private tableStatusService statusService;

    @Autowired
    private CustomerAssignmentService  customerAssignmentService;

    private static final String WAITING_LIST_KEY = "waitingList";
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try{
            String channel = new String(pattern);
            String orderKey = new String(message.getBody());//can be table no also depending on the cannel


            System.out.println("Received message from channel " + channel + " : " +orderKey);

            if (channel.equals("table-updates")) {

                processTableUpdate(orderKey);
                System.out.println("After process table updates");
            }
            if(channel.contains("order-updates")){
                processOrderUpdate(orderKey);
            }
            else if(channel.contains("order-deletes")){
                processOrderDelete(orderKey);
            }
        } catch (Exception e){
            System.err.println("Error in processing redis msg : " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void processTableUpdate(String tableNo) {
        //should update UI by showing the table as free
        customerAssignmentService.allocateTablesFromWaitingList(tableNo,Integer.parseInt(statusService.getStatus(tableNo)));
        System.out.println("table " + tableNo + " has been updated");
    }

    private void processOrderUpdate(String orderKey){
        //should call get function of order
        // Implement logic to update your UI
        // This could involve sending a WebSocket message, updating a reactive stream, etc.
        System.out.println("Processing order update : " + orderKey);
    }
    private void processOrderDelete(String orderKey){
        // should notify that the order is deleted in UI
        // Implement logic to update your UI
        // This could involve sending a WebSocket message, updating a reactive stream, etc.
        System.out.println("Processing order delete : " + orderKey);
    }
}
