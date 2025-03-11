package com.project.restaurantOrderingManagement.waitingList;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/waiting-list")
public class WaitingListController {

    @Autowired
    private WaitingListService waitingListService;

    @PostMapping("/add")
    public String addToWaitingList(@RequestBody WaitingList request) throws JsonProcessingException {
        System.out.println("Received request: " + request);
        waitingListService.addToWaitingList(request);
        return "Added to waiting list: " + request;
    }

    @GetMapping
    public List<WaitingList> getWaitingList() {
        return waitingListService.getWaitingList();
    }
}
