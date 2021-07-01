package com.example.demo.model.exception;

public class ArticleException extends Exception {
    public ArticleException() {
        super();
    }

    public ArticleException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
