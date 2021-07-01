package com.example.demo.model.exception;

public class CategoryException extends Exception {
    public CategoryException() {
        super();
    }

    public CategoryException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
