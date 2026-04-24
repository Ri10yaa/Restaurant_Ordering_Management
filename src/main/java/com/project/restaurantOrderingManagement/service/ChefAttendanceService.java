package com.project.restaurantOrderingManagement.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ChefAttendanceService {

    private static final String CHEF_KEY_PREFIX = "chef:";
    private final RedisTemplate<String, Object> redisTemplate;

    public ChefAttendanceService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void markLogin(String chefCode, LocalDateTime dateTime) {
        String key = CHEF_KEY_PREFIX + chefCode;
        redisTemplate.opsForHash().put(key, "present", "true");
        redisTemplate.opsForHash().put(key, "LastLoginTime", dateTime.toString());
    }

    public void markLogout(String chefCode, LocalDateTime dateTime) {
        String key = CHEF_KEY_PREFIX + chefCode;
        redisTemplate.opsForHash().put(key, "present", "false");
        redisTemplate.opsForHash().put(key, "LastLoginTime", dateTime.toString());
    }

    public List<String> getActiveChefCodes() {
        Set<String> keys = redisTemplate.keys(CHEF_KEY_PREFIX + "*");
        List<String> active = new ArrayList<>();
        if (keys == null) {
            return active;
        }

        for (String key : keys) {
            Object present = redisTemplate.opsForHash().get(key, "present");
            if (present != null && "true".equalsIgnoreCase(present.toString())) {
                active.add(key.replace(CHEF_KEY_PREFIX, ""));
            }
        }
        return active;
    }
}
