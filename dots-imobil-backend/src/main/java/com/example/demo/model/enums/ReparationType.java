package com.example.demo.model.enums;

public enum ReparationType {
    EURO("euro"),
    WITHOUT("without"),
    MEDIUM("medium");

    private String key;

    ReparationType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
