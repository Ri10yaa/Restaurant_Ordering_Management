//package com.project.restaurantOrderingManagement.waitingList;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.ListOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class WaitingListService {
//
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    private static final String WAITING_LIST_KEY = "waitingList";
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    public void addToWaitingList(WaitingList request) throws JsonProcessingException {
//        request.setTimeOfEntry(LocalDateTime.now());
//        String entryJson = objectMapper.writeValueAsString(request);
//        redisTemplate.opsForList().rightPush(WAITING_LIST_KEY, entryJson);
//        System.out.println("Added to waiting list: " + entryJson);
//    }
//
//    public List<WaitingList> getWaitingList() {
//        ListOperations<String, String> listOps = redisTemplate.opsForList();
//        List<String> entries = listOps.range(WAITING_LIST_KEY, 0, -1);
//
//        return entries.stream()
//                .map(entryJson -> {
//                    try {
//                        return objectMapper.readValue(entryJson, WaitingList.class);
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException("Error parsing waiting list entry", e);
//                    }
//                })
//                .collect(Collectors.toList());
//    }
//}
package com.project.restaurantOrderingManagement.waitingList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WaitingListService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String WAITING_LIST_KEY = "waitingList";

    @Autowired
    private ObjectMapper objectMapper;

    public void addToWaitingList(WaitingList request) throws JsonProcessingException {
        request.setTimeOfEntry(LocalDateTime.now());
        String entryJson = objectMapper.writeValueAsString(request);
        redisTemplate.opsForList().rightPush(WAITING_LIST_KEY, entryJson);
        System.out.println("Added to waiting list: " + entryJson);
    }

    public List<WaitingList> getWaitingList() {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        List<String> entries = listOps.range(WAITING_LIST_KEY, 0, -1);

        return entries.stream()
                .map(entryJson -> {
                    try {
                        return objectMapper.readValue(entryJson, WaitingList.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error parsing waiting list entry", e);
                    }
                })
                .collect(Collectors.toList());
    }

    public void removeFromWaitingList(String entryJson) {
        redisTemplate.opsForList().remove(WAITING_LIST_KEY, 1, entryJson);
        System.out.println("Removed assigned customer from waiting list.");
    }

}
