package com.example.demo.util;

public enum ImageKey {
    ORIGINAL("original"),
    THUMBNAIL("thumbnail");

    private String key;

    ImageKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
