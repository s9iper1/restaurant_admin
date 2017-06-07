package com.byteshaft.restaurantadmin.gettersetter;

import org.json.JSONObject;

import java.io.Serializable;


public class TableDetail  implements Serializable{

    private int id;
    private int restaurantId;
    private boolean serviceAble;
    private int tableNumber;
    private int numberOfChair;
    private int minimumBookingTime;
    private String locationInRestaurant;

    public JSONObject getBookings() {
        return bookings;
    }

    public void setBookings(JSONObject bookings) {
        this.bookings = bookings;
    }

    private JSONObject bookings;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public boolean isServiceAble() {
        return serviceAble;
    }

    public void setServiceAble(boolean serviceAble) {
        this.serviceAble = serviceAble;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getNumberOfChair() {
        return numberOfChair;
    }

    public void setNumberOfChair(int numberOfChair) {
        this.numberOfChair = numberOfChair;
    }

    public int getMinimumBookingTime() {
        return minimumBookingTime;
    }

    public void setMinimumBookingTime(int minimumBookingTime) {
        this.minimumBookingTime = minimumBookingTime;
    }

    public String getLocationInRestaurant() {
        return locationInRestaurant;
    }

    public void setLocationInRestaurant(String locationInRestaurant) {
        this.locationInRestaurant = locationInRestaurant;
    }

}
