package com.example.demo.model.enums;

public enum ConstructionType {
    NEW_BUILDING("new_building"),
    OLD_BUILDING("old_building");

    private String key;

    ConstructionType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
