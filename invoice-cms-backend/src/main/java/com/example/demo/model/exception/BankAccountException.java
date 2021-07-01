package com.example.demo.model.exception;

public class BankAccountException extends Exception {
    public BankAccountException() {
        super();
    }

    public BankAccountException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
