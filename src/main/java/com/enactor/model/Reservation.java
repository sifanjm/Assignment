package com.enactor.model;


import java.util.List;


public class Reservation {
    private String reservationNumber;
    private int passengerCount;
    private String origin;
    private String destination;
    private List<String> seatNumbers;
    private double totalPrice;
    private long timestamp;
    
    public Reservation(String reservationNumber, int passengerCount, String origin, 
                      String destination, List<String> seatNumbers, double totalPrice) {
        this.reservationNumber = reservationNumber;
        this.passengerCount = passengerCount;
        this.origin = origin;
        this.destination = destination;
        this.seatNumbers = seatNumbers;
        this.totalPrice = totalPrice;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getReservationNumber() {
        return reservationNumber;
    }
    
    public int getPassengerCount() {
        return passengerCount;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public List<String> getSeatNumbers() {
        return seatNumbers;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}