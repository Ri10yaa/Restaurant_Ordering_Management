package com.project.restaurantOrderingManagement.configuration;

import com.project.restaurantOrderingManagement.waiter.orderSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        return redisTemplate;
    }

    @Bean
    public ChannelTopic orderUpdateTopic() {
        return new ChannelTopic("order-updates");
    }

    @Bean
    public ChannelTopic orderDeleteTopic() {
        return new ChannelTopic("order-deletes");
    }

    @Bean
    public ChannelTopic tableUpdateTopic() {
        return new ChannelTopic("table-updates");
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(orderSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter messageListenerAdapter,ChannelTopic orderUpdateTopic,ChannelTopic orderDeleteTopic, ChannelTopic tableUpdateTopic) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter,orderUpdateTopic);
        container.addMessageListener(messageListenerAdapter,orderDeleteTopic);
        container.addMessageListener(messageListenerAdapter,tableUpdateTopic);
        return container;
    }

}
