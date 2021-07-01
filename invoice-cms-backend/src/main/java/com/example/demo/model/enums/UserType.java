package com.example.demo.model.enums;

public enum UserType {
    FREELANCE("FREELANCE"),
    ENTREPRENEUR("ENTREPRENEUR");

    private String key;

    UserType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
