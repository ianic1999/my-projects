package com.example.demo.model.exception;

public class SpaceException extends Exception {
    public SpaceException() {
        super();
    }

    public SpaceException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
