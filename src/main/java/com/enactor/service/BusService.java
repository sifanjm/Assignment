package com.enactor.service;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.enactor.model.Reservation;
import com.enactor.model.Seat;


public class BusService {
    private static BusService instance;
    private final List<Seat> seats;
    private final Map<String, Reservation> reservations;
    private final AtomicInteger reservationCounter;
    private final Map<String, Integer> priceMap;
    
    private BusService() {
        seats = initializeSeats();
        reservations = new ConcurrentHashMap<>();
        reservationCounter = new AtomicInteger(1);
        priceMap = initializePriceMap();
    }
    
    public static synchronized BusService getInstance() {
        if (instance == null) {
            instance = new BusService();
        }
        return instance;
    }
    
    private List<Seat> initializeSeats() {
        List<Seat> seatList = new ArrayList<>();
        char[] columns = {'A', 'B', 'C', 'D'};
        for (int row = 1; row <= 10; row++) {
            for (char col : columns) {
                seatList.add(new Seat(row + String.valueOf(col)));
            }
        }
        return seatList;
    }
    
    private Map<String, Integer> initializePriceMap() {
        Map<String, Integer> prices = new HashMap<>();
        prices.put("A-B", 50);
        prices.put("B-A", 50);
        prices.put("A-C", 100);
        prices.put("C-A", 100);
        prices.put("A-D", 150);
        prices.put("D-A", 150);
        prices.put("B-C", 50);
        prices.put("C-B", 50);
        prices.put("B-D", 100);
        prices.put("D-B", 100);
        prices.put("C-D", 50);
        prices.put("D-C", 50);
        return prices;
    }
    
 
    public synchronized List<String> getAvailableSeats(String origin, String destination, int passengerCount) {
        validateLocations(origin, destination);
        
        List<String> availableSeats = new ArrayList<>();
        for (Seat seat : seats) {
            if (seat.isAvailableForRoute(origin, destination)) {
                availableSeats.add(seat.getSeatNumber());
                if (availableSeats.size() >= passengerCount) {
                    break;
                }
            }
        }
        return availableSeats;
    }
    
   
    public int getPrice(String origin, String destination) {
        validateLocations(origin, destination);
        String key = origin.toUpperCase() + "-" + destination.toUpperCase();
        Integer price = priceMap.get(key);
        if (price == null) {
            throw new IllegalArgumentException("Invalid route: " + origin + " to " + destination);
        }
        return price;
    }
    

    public synchronized Reservation reserveTickets(int passengerCount, String origin, 
                                                   String destination, double expectedPrice) {
        validateLocations(origin, destination);
        
        if (passengerCount <= 0 || passengerCount > 40) {
            throw new IllegalArgumentException("Invalid passenger count. Must be between 1 and 40.");
        }
        
    
        int pricePerTicket = getPrice(origin, destination);
        double totalPrice = pricePerTicket * passengerCount;
        
      
        if (Math.abs(totalPrice - expectedPrice) > 0.01) {
            throw new IllegalArgumentException("Price mismatch. Expected: " + expectedPrice + 
                                             ", Actual: " + totalPrice);
        }
        
      
        List<String> availableSeats = getAvailableSeats(origin, destination, passengerCount);
        
        if (availableSeats.size() < passengerCount) {
            throw new IllegalStateException("Not enough seats available. Available: " + 
                                          availableSeats.size() + ", Required: " + passengerCount);
        }
        
       
        List<String> reservedSeats = new ArrayList<>();
        for (int i = 0; i < passengerCount; i++) {
            String seatNumber = availableSeats.get(i);
            Seat seat = findSeat(seatNumber);
            if (seat != null) {
                seat.setAvailable(false);
                seat.setReservedFrom(origin);
                seat.setReservedTo(destination);
                reservedSeats.add(seatNumber);
            }
        }
        
    
        String reservationNumber = "RES" + String.format("%06d", reservationCounter.getAndIncrement());
        Reservation reservation = new Reservation(reservationNumber, passengerCount, 
                                                 origin, destination, reservedSeats, totalPrice);
        reservations.put(reservationNumber, reservation);
        
        return reservation;
    }

    
    public Reservation getReservation(String reservationNumber) {
        return reservations.get(reservationNumber);
    }
    

    public Collection<Reservation> getAllReservations() {
        return reservations.values();
    }
    
    
    private Seat findSeat(String seatNumber) {
        for (Seat seat : seats) {
            if (seat.getSeatNumber().equals(seatNumber)) {
                return seat;
            }
        }
        return null;
    }
    
    private void validateLocations(String origin, String destination) {
        List<String> validLocations = Arrays.asList("A", "B", "C", "D");
        String originUpper = origin.toUpperCase();
        String destinationUpper = destination.toUpperCase();
        
        if (!validLocations.contains(originUpper)) {
            throw new IllegalArgumentException("Invalid origin: " + origin);
        }
        if (!validLocations.contains(destinationUpper)) {
            throw new IllegalArgumentException("Invalid destination: " + destination);
        }
        if (originUpper.equals(destinationUpper)) {
            throw new IllegalArgumentException("Origin and destination cannot be the same");
        }
    }
}