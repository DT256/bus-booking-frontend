package com.group8.busbookingapp.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Booking {
    private String id;
    private String userId;
    private String busImage;
    private String startCity;
    private String endCity;
    private int seats;
    private List<String> seatNames;
    private int totalPrice;
    private String status;
    private String paymentStatus;
    private String bookingCode;
    private String createdAt;
    private String departureTime;
    private String arrivalTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBusImage() { return busImage; }
    public void setBusImage(String busImage) { this.busImage = busImage; }

    public String getStartCity() { return startCity; }
    public void setStartCity(String startCity) { this.startCity = startCity; }

    public String getEndCity() { return endCity; }
    public void setEndCity(String endCity) { this.endCity = endCity; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    public List<String> getSeatNames() { return seatNames; }
    public void setSeatNames(List<String> seatNames) { this.seatNames = seatNames; }

    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking booking = (Booking) o;
        return seats == booking.seats &&
                Objects.equals(id, booking.id) &&
                Objects.equals(userId, booking.userId) &&
                Objects.equals(busImage, booking.busImage) &&
                Objects.equals(startCity, booking.startCity) &&
                Objects.equals(endCity, booking.endCity) &&
                Objects.equals(seatNames, booking.seatNames) &&
                Objects.equals(totalPrice, booking.totalPrice) &&
                Objects.equals(status, booking.status) &&
                Objects.equals(paymentStatus, booking.paymentStatus) &&
                Objects.equals(bookingCode, booking.bookingCode) &&
                Objects.equals(createdAt, booking.createdAt) &&
                Objects.equals(departureTime, booking.departureTime) &&
                Objects.equals(arrivalTime, booking.arrivalTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, busImage, startCity, endCity, seats, seatNames, totalPrice,
                status, paymentStatus, bookingCode, createdAt, departureTime, arrivalTime);
    }
}
