package com.example.demo.model.enums;

public enum OfferingType {
    PRODUCT("product"),
    SERVICE("service");

    private String key;

    OfferingType(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
