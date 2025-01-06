package com.project.restaurantOrderingManagement.waiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/bill")
public class billController {
    @Autowired
    private final billService billService;

    public billController(com.project.restaurantOrderingManagement.waiter.billService billService) {
        this.billService = billService;
    }

    @GetMapping("/{billno}")
    public ResponseEntity<billDTO> getBill(@PathVariable String billno) throws IOException, ClassNotFoundException {
        billDTO billDTO = billService.getBill(Long.parseLong(billno));
        return ResponseEntity.ok(billDTO);
    }
    @PostMapping("/create")
    public ResponseEntity<Long> createBill(@RequestBody billDTO bill) throws IOException {
        if (bill.getWaiterCode() == null || bill.getWaiterCode().isEmpty()) {
            throw new IllegalArgumentException("waiterCode cannot be null or empty");
        }
        System.out.println(bill.getWaiterCode());
        Long billNo = billService.storeBill(bill);
        return ResponseEntity.ok(billNo);
    }

//    @GetMapping("/close")
//    public ResponseEntity<Object> closeBill(@RequestBody String billNo) {
//        billService.closeBill(billNo);
//    }
}
