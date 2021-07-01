package com.example.demo.model.exception;

public class IndicationException extends Exception {
    public IndicationException() {
        super();
    }

    public IndicationException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
