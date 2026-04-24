package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.kitchen.AssignmentService;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Service
public class queueService {

    private final Queue<String> orderQueue = new LinkedList<>();
    private final AssignmentService assignmentService;

    public queueService(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    public void enqueue(String orderKey) {
        orderQueue.add(orderKey);
        String assignmentResult = assignmentService.assign(orderKey);
        if (assignmentResult != null && !assignmentResult.isBlank() && !assignmentResult.startsWith("No chef")) {
            orderQueue.remove(orderKey);
        }
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
