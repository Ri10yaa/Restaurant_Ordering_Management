package com.project.restaurantOrderingManagement.waiter;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class waiterAttendance {
    RedisTemplate<String, String> redisTemplate;
    String waiter_key = "waiter:";

    public waiterAttendance(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void makeAttendance(String waiterCode, LocalDateTime dateTime) throws RuntimeException{
        try{
            redisTemplate.opsForHash().put(waiter_key + waiterCode,"present","true");
            redisTemplate.opsForHash().put(waiter_key + waiterCode,"LastLoginTime",dateTime.toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void terminateAttendance(String waiterCode, LocalDateTime dateTime) {
        try{
            redisTemplate.opsForHash().put(waiter_key + waiterCode,"present","false");
            redisTemplate.opsForHash().put(waiter_key + waiterCode,"LastLoginTime",dateTime.toString());
        }
        catch(Exception e){
            throw new RuntimeException("Error in terminateAttendance:" + e.getMessage());
        }
    }
    public List<String> getAllActiveWaiters() {
        try {
            Set<String> waiters = redisTemplate.keys(waiter_key + "*");
            List<String> activeWaiters = new ArrayList<>();
            if (waiters != null) {
                for (String waiterKey : waiters) {
                    Object present = redisTemplate.opsForHash().get(waiterKey, "present");
                    if ("true".equals(present)) {
                        activeWaiters.add(waiterKey.replace(waiter_key, ""));
                    }
                }
            }
            return activeWaiters;
        } catch (Exception e) {
            throw new RuntimeException("Error in getAllActiveWaiters: " + e.getMessage(), e);
        }
    }

}
