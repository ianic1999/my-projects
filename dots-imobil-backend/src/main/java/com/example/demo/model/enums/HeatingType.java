package com.example.demo.model.enums;

public enum HeatingType {
    AUTONOMOUS("autonomous"),
    CENTRAL("central");

    private String key;

    HeatingType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
