package com.project.restaurantOrderingManagement.helpers;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class BillNoIncrementingService {

    private static final String BILL_NO = "billNo";

    private final RedisTemplate<String, Object> redisTemplate;

    public BillNoIncrementingService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setBillNo() {
        redisTemplate.opsForValue().set(BILL_NO, 0);
    }

    public long getBillNo() {
        Object value = redisTemplate.opsForValue().get(BILL_NO);
        return value == null ? 0 : Long.parseLong(value.toString());
    }

    public long incrementBillNo() {
        return redisTemplate.opsForValue().increment(BILL_NO, 1);
    }
}
