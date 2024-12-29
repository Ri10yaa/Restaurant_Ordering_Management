package com.project.restaurantOrderingManagement.waiter;

public class billDTO {
    private long billNo;
    private String waitercode;
    private int tableNo;

    public billDTO(long billNo, String waitercode, int tableNo) {
        this.billNo = billNo;
        this.waitercode = waitercode;

    }

    public long getBillNo() {
        return billNo;
    }

    public void setBillNo(long billNo) {
        this.billNo = billNo;
    }

    public String getWaitercode() {
        return waitercode;
    }

    public void setWaitercode(String waitercode) {
        this.waitercode = waitercode;
    }
    public int getTableNo() {
        return tableNo;
    }
    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
    }
}
