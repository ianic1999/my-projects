package com.example.demo.model.enums;

public enum HouseType {
    COTTAGE("cottage"),
    TOWNHOUSE("townhouse"),
    HOUSE("house");

    private String key;

    HouseType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
