package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.repositories.tableRepo;
import com.project.restaurantOrderingManagement.waiter.tablePublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class tableStatusService {
    public final RedisTemplate<String,Object> redisTemplate;
    private final String key = "tableStatus";
    private final tableRepo tableRepo;
    private final tablePublisher tablePublisher;

    public tableStatusService(RedisTemplate<String,Object> redisTemplate, tableRepo tableRepo, com.project.restaurantOrderingManagement.waiter.tablePublisher tablePublisher) {
        this.redisTemplate = redisTemplate;
        this.tableRepo = tableRepo;
        this.tablePublisher = tablePublisher;
    }

    public void resetTableStatus(){
        List<table> tables = tableRepo.findAll();
        for (com.project.restaurantOrderingManagement.models.table table : tables) {
            redisTemplate.opsForHash().put(key, String.valueOf(table.getTableNo()),String.valueOf(table.getNoOfSeats()));
        }
    }

    @Async
    public void markEngaged(int tableNo, int seatsEngaged){
        try{
            Integer seatsAvailable = Integer.parseInt(redisTemplate.opsForHash().get(key,String.valueOf(tableNo)).toString());
            if(seatsAvailable < seatsEngaged){
                System.out.println("Seats are not enough");
            }
            else{
                redisTemplate.opsForHash().put(key, String.valueOf(tableNo),String.valueOf(seatsAvailable-seatsEngaged));
                tablePublisher.publishTableUpdate(String.valueOf(tableNo));
            }

        }
        catch (Exception e){
            System.err.println("Error while making engaged : " + e.getMessage());
        }
    }

    @Async
    public void markVacant(int tableNo,int seatsFreed){
        try{
            Integer seatsAvailable = Integer.parseInt(redisTemplate.opsForHash().get(key,String.valueOf(tableNo)).toString());
            redisTemplate.opsForHash().put(key, String.valueOf(tableNo),String.valueOf(seatsAvailable+seatsFreed));
            tablePublisher.publishTableUpdate(String.valueOf(tableNo));
        }
        catch (Exception e){
            System.err.println("Error while making vacant : " + e.getMessage());
        }
    }

    public String getStatus(String tableNo) {
        try {
            String status = (redisTemplate.opsForHash().get(key, String.valueOf(tableNo)) != null) ? redisTemplate.opsForHash().get(key, String.valueOf(tableNo)).toString() : "Not Found";

//            if (statusObj == null) {
//                System.out.println("No status found for table " + tableNO);
//                return "Not Found";  // Return a default value or handle it as needed
//            }


            System.out.println("The status of " + tableNo + " is " + status);
            return status;
        } catch (Exception e) {
            System.err.println("Error while fetching status :( : " + e.getMessage());
        }
        return "";
    }

}

