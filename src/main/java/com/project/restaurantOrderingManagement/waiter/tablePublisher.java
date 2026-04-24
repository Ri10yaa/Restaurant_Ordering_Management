package com.project.restaurantOrderingManagement.waiter;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class tablePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic tableUpdateTopic;

    public tablePublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic tableUpdateTopic) {
        this.redisTemplate = redisTemplate;
        this.tableUpdateTopic = tableUpdateTopic;
    }

    public void publishTableUpdate(String tableNo) {
        redisTemplate.convertAndSend(tableUpdateTopic.getTopic(), tableNo);
    }
}
