package com.group8.busbookingapp.dto;

public class UpdateUserRequest {
    private String username;
    private String phoneNumber;
    private String dateOfBirth;
    private int gender;

    public UpdateUserRequest(){}

    public String getFullName() {
        return username;
    }

    public void setFullName(String fullName) {
        this.username = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
