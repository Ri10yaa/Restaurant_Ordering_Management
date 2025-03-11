package com.project.restaurantOrderingManagement.waiter;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class orderSubscriber implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try{
            String channel = new String(pattern);
            String orderKey = new String(message.getBody());

            System.out.println("Received message from channel " + channel + " : " +orderKey);

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
