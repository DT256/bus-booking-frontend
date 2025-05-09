package com.group8.busbookingapp.model;

import java.time.LocalDateTime;

public class StopPoint {
    private String id;
    private String name;
    private String address;
    private Integer orderNumber;
    private String type;
    private String estimatedTime;
    private boolean isSelected;

    public StopPoint(String id, String name, String address, Integer orderNumber, String type, String estimatedTime) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.orderNumber = orderNumber;
        this.type = type;
        this.estimatedTime = estimatedTime;
        this.isSelected = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
} 