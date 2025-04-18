package com.group8.busbookingapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
public class Trip implements Serializable {
    private String id;
    private String busId;
    private int capacity;
    private String avatar;
    private String departureTime;
    private String arrivalTime;
    private int price;
    private String startPointCity;
    private String endPointCity;
    private double distance;
    private int duration;
    private int availableSeats;
    private String busType;
    private String status;

    public Trip(String id, String busId, int capacity, String avatar, String departureTime, String arrivalTime, int price, String startPointCity, String endPointCity, int availableSeats, String busType, String status) {
        this.id = id;
        this.busId = busId;
        this.capacity = capacity;
        this.avatar = avatar;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.startPointCity = startPointCity;
        this.endPointCity = endPointCity;
        this.availableSeats = availableSeats;
        this.busType = busType;
        this.status = status;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
