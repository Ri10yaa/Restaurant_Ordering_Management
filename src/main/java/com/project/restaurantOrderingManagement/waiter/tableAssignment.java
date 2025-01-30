package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.models.table;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import com.project.restaurantOrderingManagement.repositories.tableRepo;

import java.util.List;
import java.util.*;

public class tableAssignment {
    tableRepo tableRepo;
    empRepo empRepo;
    private Map<String, List<Integer>> assignTable() {
        List<table> tables = tableRepo.findAll();
        List
    }
}
