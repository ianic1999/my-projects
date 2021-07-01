package com.example.demo.model.enums;

public enum UserProvider {
    LOCAL("LOCAL"),
    GOOGLE("GOOGLE");

    private String key;

    UserProvider(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
