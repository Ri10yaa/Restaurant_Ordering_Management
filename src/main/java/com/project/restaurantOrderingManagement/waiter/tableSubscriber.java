package com.project.restaurantOrderingManagement.waiter;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class tableSubscriber implements MessageListener {
    @Override
    public void onMessage(Message msg,byte[] pattern) {
        try{
            String channel = new String(pattern);
            String tableNo = new String(msg.getBody());

            System.out.println("Received message from channel " + channel + " with key " + tableNo);

            if(channel.equals("table-updates")){
                processTableUpdate(tableNo);
            }
        } catch (Exception e) {
            System.err.println("Error processing table updates : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processTableUpdate(String tableNo) {
        //should update UI by showing the table as free
        System.out.println("Table " + tableNo + " has been updated");
    }
}
