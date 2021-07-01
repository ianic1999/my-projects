package com.example.demo.model.exception;

public class PartnerException extends Exception {
    public PartnerException() {
        super();
    }

    public PartnerException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
