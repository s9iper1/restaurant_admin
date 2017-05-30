package com.byteshaft.restaurantadmin.gettersetter;

/**
 * Created by s9iper1 on 5/30/17.
 */

public class OrderDetails {

    private String TableNumber;
    private String startEndTime;
    private String orderDetails;

    public String getTableNumber() {
        return TableNumber;
    }

    public void setTableNumber(String tableNumber) {
        TableNumber = tableNumber;
    }

    public String getStartEndTime() {
        return startEndTime;
    }

    public void setStartEndTime(String startEndTime) {
        this.startEndTime = startEndTime;
    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }

}
