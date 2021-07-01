package com.example.demo.model.exception;

public class AgentException extends Exception {
    public AgentException() {
        super();
    }

    public AgentException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
