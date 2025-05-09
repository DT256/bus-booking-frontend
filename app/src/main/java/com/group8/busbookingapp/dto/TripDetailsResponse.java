package com.group8.busbookingapp.dto;

import com.group8.busbookingapp.model.StopPoint;

import java.util.List;

public class TripDetailsResponse {
    private String id;
    private String startPointCity;
    private String endPointCity;
    private String departureTime;
    private double price;
    private int capacity;
    private String busType;
    private List<Seat> seats;
    private List<StopPoint> stopPoints;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartPointCity() {
        return startPointCity;
    }

    public void setStartPointCity(String startPointCity) {
        this.startPointCity = startPointCity;
    }

    public String getEndPointCity() {
        return endPointCity;
    }

    public void setEndPointCity(String endPointCity) {
        this.endPointCity = endPointCity;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public List<StopPoint> getStopPoints() {
        return stopPoints;
    }

    public void setStopPoints(List<StopPoint> stopPoints) {
        this.stopPoints = stopPoints;
    }
}

