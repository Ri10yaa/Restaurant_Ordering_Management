package com.project.restaurantOrderingManagement.waiter;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

@Service
public class waiterAttendance {
    RedisTemplate<String, Object> redisTemplate;
    String waiter_key = "waiter:";

    public waiterAttendance(RedisTemplate<String,Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void makeAttendance(String waiterCode, LocalDateTime dateTime) {
        try {
            String key = waiter_key + waiterCode;

            if (!redisTemplate.hasKey(key)) {
                redisTemplate.opsForHash().put(key, "present", "true");
                redisTemplate.opsForHash().put(key, "LastLoginTime", dateTime.toString());
            } else {
                redisTemplate.opsForHash().put(key, "LastLoginTime", dateTime.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error in makeAttendance: " + e.getMessage(), e);
        }
    }

    public void terminateAttendance(String waiterCode, LocalDateTime dateTime) {
        try {
            String key = waiter_key + waiterCode;
            redisTemplate.opsForHash().put(key, "present", "false");
            redisTemplate.opsForHash().put(key, "LastLoginTime", dateTime.toString());
        } catch (Exception e) {
            throw new RuntimeException("Error in terminateAttendance: " + e.getMessage(), e);
        }
    }

    public List<String> getAllActiveWaiters() {
        try {
            Set<String> waiterKeys = redisTemplate.keys(waiter_key + "*");
            List<String> activeWaiters = new ArrayList<>();

            if (waiterKeys != null) {
                for (String waiterKey : waiterKeys) {
                    Object present = redisTemplate.opsForHash().get(waiterKey, "present");
                    System.out.println(present.toString());
                    if (present!=null && present.toString().equalsIgnoreCase("true")) {
                        String waiterCode = waiterKey.replace(waiter_key, "");
                        System.out.println(waiterCode);
                        activeWaiters.add(waiterCode);
                    }
                }
            }

            return activeWaiters;
        } catch (Exception e) {
            throw new RuntimeException("Error in getAllActiveWaiters: " + e.getMessage(), e);
        }
    }

}
