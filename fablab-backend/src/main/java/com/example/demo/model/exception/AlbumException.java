package com.example.demo.model.exception;

public class AlbumException extends Exception {
    public AlbumException() {
        super();
    }

    public AlbumException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
