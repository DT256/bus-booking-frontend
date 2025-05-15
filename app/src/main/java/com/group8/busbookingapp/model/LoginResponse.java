package com.group8.busbookingapp.model;

import java.time.LocalDateTime;

public class LoginResponse {
    private String jwt;
    private String message;

    private String username;

    private String avatarUrl;
    private String phoneNumber;
    private String gender;
    private String dateOfBirth;
    public String getJwt() {
        return jwt;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
}
