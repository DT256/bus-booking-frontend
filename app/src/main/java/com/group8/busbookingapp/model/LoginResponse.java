package com.group8.busbookingapp.model;

public class LoginResponse {
    private String jwt;
    private String message;

    private String username;

    public String getJwt() {
        return jwt;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }
}
