package com.example.demo.model.exception;

public class AuthenticationException extends Exception {
    private String field;

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String field, String message) {
        super(message);
        this.field = field;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public String getField() {
        return field;
    }
}
