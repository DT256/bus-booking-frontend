package com.group8.busbookingapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TripDetailsResponse {
    private String id;
    private String busId;
    private String licensePlate;
    private int capacity;
    private String departureTime;
    private double price;
    private String status;
    private String startPointCity;
    private String endPointCity;
    private List<Seat> seats;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}

