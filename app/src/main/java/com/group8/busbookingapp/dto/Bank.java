package com.group8.busbookingapp.dto;

public class Bank {
    private String name;
    private String code;
    private String shortName;
    private String logo;

    public Bank(String name, String code, String shortName, String logo) {
        this.name = name;
        this.code = code;
        this.shortName = shortName;
        this.logo = logo;
    }

    public String getName() { return name; }
    public String getCode() { return code; }
    public String getShortName() { return shortName; }
    public String getLogo() { return logo; }

    @Override
    public String toString() {
        return shortName; // dùng để hiển thị trong Spinner
    }
}
