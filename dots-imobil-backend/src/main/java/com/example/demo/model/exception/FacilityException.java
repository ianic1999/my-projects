package com.example.demo.model.exception;

public class FacilityException extends Exception {
    public FacilityException() {
        super();
    }

    public FacilityException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
