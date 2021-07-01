package com.example.demo.model.exception;

public class CustomerException extends Exception {
    public CustomerException() {
        super();
    }

    public CustomerException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
