package com.enactor.model;


public class Seat {
    private String seatNumber;
    private boolean available;
    private String reservedFrom;
    private String reservedTo;
    
    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
        this.available = true;
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public String getReservedFrom() {
        return reservedFrom;
    }
    
    public void setReservedFrom(String reservedFrom) {
        this.reservedFrom = reservedFrom;
    }
    
    public String getReservedTo() {
        return reservedTo;
    }
    
    public void setReservedTo(String reservedTo) {
        this.reservedTo = reservedTo;
    }
    
    public boolean isAvailableForRoute(String from, String to) {
        if (available) {
            return true;
        }
        
        return false;
    }
}