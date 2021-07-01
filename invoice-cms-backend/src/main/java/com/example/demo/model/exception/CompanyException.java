package com.example.demo.model.exception;

public class CompanyException extends Exception {
    public CompanyException() {
        super();
    }

    public CompanyException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
