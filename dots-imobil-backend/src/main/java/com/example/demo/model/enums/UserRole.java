package com.example.demo.model.enums;

public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER");

    private String key;

    UserRole(String role) {
        this.key = role;
    }

    public String getKey() {
        return key;
    }
}
