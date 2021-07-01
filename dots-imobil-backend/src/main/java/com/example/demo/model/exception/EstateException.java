package com.example.demo.model.exception;

public class EstateException extends Exception {
    public EstateException() {
        super();
    }

    public EstateException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
