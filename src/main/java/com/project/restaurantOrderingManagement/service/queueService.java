package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.kitchen.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Service
public class queueService {

    private final Queue<String> orderQueue = new LinkedList<>();
    private final AssignmentService assignmentService;

    @Autowired
    public queueService(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    public void enqueue(String orderKey) {
        System.out.println("Enqueueing order with key: " + orderKey);
        orderQueue.add(orderKey);
        String[] parts = orderKey.split(":");
        String billNo = parts[2];
        String assignedOrder = assignmentService.assign(orderKey,billNo);
    }

    public String dequeue() {
        return orderQueue.poll();
    }

    public boolean isEmpty() {
        return orderQueue.isEmpty();
    }

    public Queue<String> getQueue() {
        return orderQueue;
    }

    public int size() {
        return orderQueue.size();
    }
}
