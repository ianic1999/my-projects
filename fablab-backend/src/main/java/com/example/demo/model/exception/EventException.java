package com.example.demo.model.exception;

public class EventException extends Exception {
    public EventException() {
        super();
    }

    public EventException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
