package com.project.restaurantOrderingManagement.waitingList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/waiting-list")
public class WaitingListController {

    private final WaitingListService waitingListService;

    public WaitingListController(WaitingListService waitingListService) {
        this.waitingListService = waitingListService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToWaitingList(@RequestBody WaitingList request) {
        waitingListService.addToWaitingList(request);
        return ResponseEntity.ok("Customer added to waiting list");
    }

    @GetMapping
    public ResponseEntity<List<WaitingList>> getWaitingList() {
        return ResponseEntity.ok(waitingListService.getWaitingList());
    }
}
