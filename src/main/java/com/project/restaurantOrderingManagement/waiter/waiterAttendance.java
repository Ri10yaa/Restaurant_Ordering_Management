package com.project.restaurantOrderingManagement.waiter;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class waiterAttendance {

    private static final String WAITER_KEY_PREFIX = "waiter:";

    private final RedisTemplate<String, Object> redisTemplate;

    public waiterAttendance(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void makeAttendance(String waiterCode, LocalDateTime dateTime) {
        String key = WAITER_KEY_PREFIX + waiterCode;
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForHash().put(key, "present", "true");
        }
        redisTemplate.opsForHash().put(key, "LastLoginTime", dateTime.toString());
        redisTemplate.opsForHash().put(key, "present", "true");
    }

    public void terminateAttendance(String waiterCode, LocalDateTime dateTime) {
        String key = WAITER_KEY_PREFIX + waiterCode;
        redisTemplate.opsForHash().put(key, "present", "false");
        redisTemplate.opsForHash().put(key, "LastLoginTime", dateTime.toString());
    }

    public List<String> getAllActiveWaiters() {
        Set<String> waiterKeys = redisTemplate.keys(WAITER_KEY_PREFIX + "*");
        List<String> activeWaiters = new ArrayList<>();

        if (waiterKeys == null) {
            return activeWaiters;
        }

        for (String waiterKey : waiterKeys) {
            Object present = redisTemplate.opsForHash().get(waiterKey, "present");
            if (present != null && "true".equalsIgnoreCase(present.toString())) {
                activeWaiters.add(waiterKey.replace(WAITER_KEY_PREFIX, ""));
            }
        }

        return activeWaiters;
    }
}
