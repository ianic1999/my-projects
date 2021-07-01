package com.example.demo.model.enums;

public enum InvoiceStatus {
    PAID("paid"),
    UNPAID("unpaid"),
    DRAFT("draft");

    private String key;

    InvoiceStatus(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
