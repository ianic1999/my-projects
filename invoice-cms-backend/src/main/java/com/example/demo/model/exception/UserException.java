package com.example.demo.model.exception;

import java.util.List;

public class UserException extends Exception {
    private String field;

    public UserException() {
        super();
    }


    public UserException(String message) {
        super(message);
    }

    public UserException(String field, String message) {
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
