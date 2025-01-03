package com.project.restaurantOrderingManagement.service;

import com.project.restaurantOrderingManagement.exceptions.DeleteOperationException;
import com.project.restaurantOrderingManagement.exceptions.EntityNotFoundException;
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

    public void deleteLog(long billno) {
        if(logRepo.existsById(billno)) {
            try{
                logRepo.deleteById(billno);
            }
            catch(Exception e) {
                throw new DeleteOperationException("Error while deleting log with bill number " + billno,e);
            }
        }
        else{
            throw new EntityNotFoundException("Log with code "+billno+" does not exist");
        }
    }

}
