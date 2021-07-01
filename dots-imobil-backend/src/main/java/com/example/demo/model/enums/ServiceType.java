package com.example.demo.model.enums;

public enum ServiceType {
    RENT("rent"),
    SELL("sell");

    private String key;

    ServiceType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
