package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.models.Log;
import com.project.restaurantOrderingManagement.repositories.logRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class logService {
    @Autowired
    logRepo logRepo;

    public logService(logRepo logRepo) {
        this.logRepo = logRepo;
    }

    public Log getLog(int id) {
        return logRepo.findById(id).get();
    }

    public List<Log> getAllLogsByWaiter(String code) {
        return logRepo.findAllByWaiterCode(code);
    }

    public List<Log> getAllLogsByDate(Date date) {
        return logRepo.findAllByDate(date);
    }

    public Log addLog(Log log) {
        return logRepo.save(log);
    }

    public void deleteLog(int billno) {
        logRepo.deleteById(billno);
    }

}