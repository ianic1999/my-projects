package com.example.demo.model.exception;

public class InvoiceException extends Exception {
    public InvoiceException() {
        super();
    }

    public InvoiceException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
