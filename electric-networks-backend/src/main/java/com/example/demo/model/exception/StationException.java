package com.example.demo.model.exception;

public class StationException extends Exception {
    public StationException() {
        super();
    }

    public StationException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
