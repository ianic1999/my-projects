package com.example.demo.model.exception;

import java.util.List;

public class UserException extends Exception {
    private List<String> messages;

    public UserException(List<String> messages) {
        super();
        this.messages = messages;
    }

    public UserException(String message) {
        super(message);
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
