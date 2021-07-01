package com.example.demo.model.enums;

public enum LandDestinationType {
    CONSTRUCTION("construction"),
    AGRICULTURAL("agricultural"),
    FRUIT_GROWING("fruit-growing");

    private String key;

    LandDestinationType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
