package com.example.demo.model.exception;

import java.util.List;

public class UserException extends Exception {
    private List<String> messages;

    public UserException(List<String> messages) {
        super();
        this.messages = messages;
    }

    public UserException() {
        super();
    }

    public UserException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public List<String> getMessages() {
        return messages;
    }
}
