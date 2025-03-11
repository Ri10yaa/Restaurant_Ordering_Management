package com.project.restaurantOrderingManagement.waiter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class OrderPublisher {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ChannelTopic orderUpdateTopic;
    @Autowired
    private ChannelTopic orderDeleteTopic;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void publishOrderUpdate(String orderkey) {
        try{
           redisTemplate.convertAndSend(orderUpdateTopic.getTopic(),orderkey);
           System.out.println("Published Order : " + orderkey);
        }catch (Exception e){
            System.err.println("Error publishing order : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void publishOrderDelete(String orderkey) {
        try{
            redisTemplate.convertAndSend(orderDeleteTopic.getTopic(),orderkey);
            System.out.println("Published Order : " + orderkey);
        }catch (Exception e){
            System.err.println("Error publishing order delete : " + e.getMessage());
        }
    }
}
