package com.project.restaurantOrderingManagement.waitingList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantOrderingManagement.exceptions.BadRequestException;
import com.project.restaurantOrderingManagement.exceptions.OperationFailedException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WaitingListService {

    private static final String WAITING_LIST_KEY = "waitingList";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public WaitingListService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void addToWaitingList(WaitingList request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Customer name is required for waiting list entry");
        }
        if (request.getSeatsRequired() <= 0) {
            throw new BadRequestException("seatsRequired must be greater than 0");
        }

        request.setTimeOfEntry(LocalDateTime.now());

        try {
            String entryJson = objectMapper.writeValueAsString(request);
            redisTemplate.opsForList().rightPush(WAITING_LIST_KEY, entryJson);
        } catch (JsonProcessingException e) {
            throw new OperationFailedException("Failed to serialize waiting list entry", e);
        }
    }

    public List<WaitingList> getWaitingList() {
        ListOperations<String, Object> listOps = redisTemplate.opsForList();
        List<Object> entries = listOps.range(WAITING_LIST_KEY, 0, -1);

        if (entries == null || entries.isEmpty()) {
            return new ArrayList<>();
        }

        return entries.stream()
                .map(Object::toString)
                .map(entryJson -> {
                    try {
                        return objectMapper.readValue(entryJson, WaitingList.class);
                    } catch (JsonProcessingException e) {
                        throw new OperationFailedException("Failed to parse waiting list entry", e);
                    }
                })
                .collect(Collectors.toList());
    }

    public void removeFromWaitingList(String entryJson) {
        redisTemplate.opsForList().remove(WAITING_LIST_KEY, 1, entryJson);
    }
}
