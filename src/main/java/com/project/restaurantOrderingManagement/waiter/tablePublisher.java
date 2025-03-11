package com.project.restaurantOrderingManagement.waiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class tablePublisher {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic tableUpdateTopic;

    public void publishTableUpdate(String tableNo) {
        try{
            redisTemplate.convertAndSend(tableNo, tableUpdateTopic);
            System.out.println("table updated : " + tableNo);
        }catch (Exception e){
            System.err.println("table update failed");
            e.printStackTrace();
        }
    }
}
