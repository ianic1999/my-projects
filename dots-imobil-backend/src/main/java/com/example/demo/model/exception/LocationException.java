package com.example.demo.model.exception;

public class LocationException extends Exception {
    public LocationException() {
        super();
    }

    public LocationException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
