package com.example.demo.model.exception;

public class TestimonialException extends Exception {
    public TestimonialException() {
        super();
    }

    public TestimonialException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
