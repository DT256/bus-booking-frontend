package com.group8.busbookingapp.dto;

import java.math.BigDecimal;
import java.util.List;

public class BookingRequest {
    private String tripId;
    private List<String> seatIds;
    private BigDecimal totalPrice;
    private PassengerDetail passengerDetail;
    private String pickupStopPointId;
    private String dropoffStopPointId;

    public static class PassengerDetail {
        private String fullName;
        private String phoneNumber;
        private String email;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public void setPassengerDetail(PassengerDetail passengerDetail) {
        this.passengerDetail = passengerDetail;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public List<String> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<String> seatIds) {
        this.seatIds = seatIds;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPickupStopPointId() {
        return pickupStopPointId;
    }

    public void setPickupStopPointId(String pickupStopPointId) {
        this.pickupStopPointId = pickupStopPointId;
    }

    public String getDropoffStopPointId() {
        return dropoffStopPointId;
    }

    public void setDropoffStopPointId(String dropoffStopPointId) {
        this.dropoffStopPointId = dropoffStopPointId;
    }

    @Override
    public String toString() {
        return "BookingRequest{" +
                ", tripId='" + tripId + '\'' +
                ", seatIds=" + seatIds +
                ", totalPrice=" + totalPrice +
                ", passengerDetail=" + passengerDetail +
                ", pickupStopPointId='" + pickupStopPointId + '\'' +
                ", dropoffStopPointId='" + dropoffStopPointId + '\'' +
                '}';
    }
}
