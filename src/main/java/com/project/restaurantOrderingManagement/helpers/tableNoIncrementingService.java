package com.project.restaurantOrderingManagement.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class tableNoIncrementingService {
    private static final String counter_key  ="tableNoCounter";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setTableNo() {
        redisTemplate.opsForValue().set(counter_key, 0);
    }
    public Integer getCurrentTableNo() {
        Integer currentTableNo = (Integer) redisTemplate.opsForValue().get(counter_key);
        return currentTableNo == null ? 0 : currentTableNo;
    }

    public Integer incrementTableNo() {
        return Math.toIntExact(redisTemplate.opsForValue().increment(counter_key, 1));
    }
}
