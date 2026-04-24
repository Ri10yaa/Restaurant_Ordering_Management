package com.project.restaurantOrderingManagement.waiter;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class OrderPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic orderUpdateTopic;
    private final ChannelTopic orderDeleteTopic;

    public OrderPublisher(RedisTemplate<String, Object> redisTemplate,
                          ChannelTopic orderUpdateTopic,
                          ChannelTopic orderDeleteTopic) {
        this.redisTemplate = redisTemplate;
        this.orderUpdateTopic = orderUpdateTopic;
        this.orderDeleteTopic = orderDeleteTopic;
    }

    public void publishOrderUpdate(String orderKey) {
        redisTemplate.convertAndSend(orderUpdateTopic.getTopic(), orderKey);
    }

    public void publishOrderDelete(String orderKey) {
        redisTemplate.convertAndSend(orderDeleteTopic.getTopic(), orderKey);
    }
}
