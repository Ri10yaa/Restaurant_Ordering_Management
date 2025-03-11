package com.project.restaurantOrderingManagement.helpers;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class  BillNoIncrementingService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String BILL_NO = "billNo";

    public BillNoIncrementingService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    public void setBillNo() {
        redisTemplate.opsForValue().set(BILL_NO, 0);

    }

    public long getBillNo() {
        return (long) redisTemplate.opsForValue().get(BILL_NO);
    }
    public long incrementBillNo() {
        return redisTemplate.opsForValue().increment(BILL_NO, 1);
    }
}
