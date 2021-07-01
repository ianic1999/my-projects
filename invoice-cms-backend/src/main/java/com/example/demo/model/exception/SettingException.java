package com.example.demo.model.exception;

public class SettingException extends Exception {
    public SettingException() {
        super();
    }

    public SettingException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
