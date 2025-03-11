package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.models.Table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import com.project.restaurantOrderingManagement.waiter.tablePublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class tableStatusService {
    private final RedisTemplate<String,Object> redisTemplate;
    private final String key = "tableStatus";
    private final tableRepo tableRepo;
    private final tablePublisher tablePublisher;

    public tableStatusService(RedisTemplate<String,Object> redisTemplate, tableRepo tableRepo, com.project.restaurantOrderingManagement.waiter.tablePublisher tablePublisher) {
        this.redisTemplate = redisTemplate;
        this.tableRepo = tableRepo;
        this.tablePublisher = tablePublisher;
    }

    public void resetTableStatus(){
        List<Table> tables = tableRepo.findAll();
        for (Table table : tables) {
            redisTemplate.opsForHash().put(key, String.valueOf(table.getTableNo()),String.valueOf(0));
        }
    }

    public void markEngaged(int tableNo){
        try{
            redisTemplate.opsForHash().put(key, String.valueOf(tableNo),String.valueOf(1));
            tablePublisher.publishTableUpdate(String.valueOf(tableNo));
        }
        catch (Exception e){
            System.err.println("Error while making engaged : " + e.getMessage());
        }
    }

    public void markVacant(int tableNo){
        try{
            redisTemplate.opsForHash().put(key, String.valueOf(tableNo),String.valueOf(0));
            tablePublisher.publishTableUpdate(String.valueOf(tableNo));
        }
        catch (Exception e){
            System.err.println("Error while making vacant : " + e.getMessage());
        }
    }
}
