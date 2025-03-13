package com.project.restaurantOrderingManagement.waiter;

import com.project.restaurantOrderingManagement.helpers.BillNoIncrementingService;
import org.springframework.beans.factory.annotation.Autowired;

public class billDTO {
    private long billNo;
    private String waiterCode;
    private int tableNo;
    private int NoOfPersons;

    public billDTO(long billNo, String waiterCode, int tableNo, int NoOfPersons) {
        this.billNo = billNo;
        this.waiterCode = waiterCode;
        this.tableNo = tableNo;
        this.NoOfPersons = NoOfPersons;
    }

    public billDTO() {}

    public int getNoOfPersons() {
        return NoOfPersons;
    }

    public void setNoOfPersons(int noOfPersons) {
        NoOfPersons = noOfPersons;
    }

    public long getBillNo() {
        return billNo;
    }

    public void setBillNo(long billNo) {
        this.billNo = billNo;
    }

    public int getTableNo() {
        return tableNo;
    }

    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
    }

    public String getWaiterCode() {
        return waiterCode;
    }

    public void setWaiterCode(String waiterCode) {
        this.waiterCode = waiterCode;
    }
}
