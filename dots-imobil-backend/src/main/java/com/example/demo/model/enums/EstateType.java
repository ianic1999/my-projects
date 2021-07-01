package com.example.demo.model.enums;

public enum EstateType {
    APARTMENT("apartment"),
    HOUSE("house"),
    LAND("land"),
    NON_RESIDENTIAL_SPACES("non-residential-spaces");

    private String key;

    EstateType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
