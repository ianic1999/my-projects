package com.example.demo.model.enums;

public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER");

    private String key;

    UserRole(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
