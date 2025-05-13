package com.group8.busbookingapp.model;

import java.util.Date;

public class ChatMessage {
    private String role; // "user" or "assistant"
    private String content;
    private Date timestamp; // Thêm timestamp cho local storage

    public ChatMessage() {
        this.timestamp = new Date();
    }

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = new Date();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
