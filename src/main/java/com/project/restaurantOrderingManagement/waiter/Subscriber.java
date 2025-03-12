package com.project.restaurantOrderingManagement.waiter;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class orderSubscriber implements MessageListener {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private tableStatusService statusService;

    private static final String WAITING_LIST_KEY = "waitingList";
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try{
            String channel = new String(pattern);
            String orderKey = new String(message.getBody());//can be table no also depending on the cannel


            System.out.println("Received message from channel order diff" + channel + " : " +orderKey);

            if (channel.equals("table-updates")) {

                processTableUpdate(orderKey);
                System.out.println("After process table updates");

                if (statusService != null) {
                    System.out.println("Status : " + statusService.getStatus(orderKey));
                }
                else{
                    System.out.println("null");
                }

                if(statusService.getStatus(orderKey).equals("0")) {
                    System.out.println("Assign table ");
                    assignTable(orderKey);
                }
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
        System.out.println("table " + tableNo + " has been updated");
    }
    private void assignTable(String tableNo) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        List<String> entries = listOps.range(WAITING_LIST_KEY, 0, -1);

        if (entries == null || entries.isEmpty()) {
            System.out.println("No one in the waiting list.");
            return;
        }

        for (String entryJson : entries) {
            try {
                WaitingList customer = objectMapper.readValue(entryJson, WaitingList.class);
                System.out.println("Assigning " + customer.getName() + " to table " + tableNo);

                // Remove the assigned customer from the waiting list
                listOps.leftPop(WAITING_LIST_KEY);
                break;
            } catch (JsonProcessingException e) {
                System.err.println("Error parsing waiting list entry: " + e.getMessage());
            }
        }
    }
    private void processOrderUpdate(String orderKey){
        //should call get function of order
        // Implement logic to update your UI
        // This could involve sending a WebSocket message, updating a reactive stream, etc.
        System.out.println("Processing order update : " + orderKey);
    }
    private void processOrderDelete(String orderKey){
        // should notigy that the order is deleted in UI
        // Implement logic to update your UI
        // This could involve sending a WebSocket message, updating a reactive stream, etc.
        System.out.println("Processing order delete : " + orderKey);
    }
}
