package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.table;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class tableServiceRedis {
    private final RedisTemplate<String,Object> redisTemplate;

    public tableServiceRedis(RedisTemplate<String,Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void resetTableStatus(List<table> tables){
        for (table table : tables) {
            redisTemplate.opsForValue().set("table:" + table.getTableNo(),"available");
        }
    }

    public void updateTableStatus(int tableNo){

        redisTemplate.opsForValue().set("table:" + tableNo,"available");
    }
}
