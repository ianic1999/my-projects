package com.example.demo.model.exception;

public class OfferingException extends Exception {
    public OfferingException() {
        super();
    }

    public OfferingException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
