package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.OrderNotFoundException;
import com.project.restaurantOrderingManagement.models.Log;
import com.project.restaurantOrderingManagement.repositories.logRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class logService {

    private final logRepo logRepo;

    public logService(logRepo logRepo) {
        this.logRepo = logRepo;
    }

    public Log getLog(long billNo) {
        return logRepo.findById(billNo)
                .orElseThrow(() -> new OrderNotFoundException("Log with bill number " + billNo + " does not exist"));
    }

    public List<Log> getAllLogsByWaiter(String code) {
        return logRepo.findAllByWaiterCode(code);
    }

    public List<Log> getAllLogsByDate(LocalDate date) {
        return logRepo.findAllByDate(date);
    }

    public Log addLog(Log log) {
        return logRepo.save(log);
    }

    public void deleteLog(long billNo) {
        if (!logRepo.existsById(billNo)) {
            throw new OrderNotFoundException("Log with bill number " + billNo + " does not exist");
        }
        logRepo.deleteById(billNo);
    }
}
