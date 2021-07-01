package com.example.demo.controller.advice;

import com.example.demo.controller.AuthenticationController;
import com.example.demo.dto.response.ExceptionResponse;
import com.example.demo.model.exception.AuthenticationException;
import com.example.demo.model.exception.CompanyException;
import com.example.demo.model.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice(basePackageClasses = {AuthenticationController.class})
@Slf4j
public class AuthenticationControllerAdvice {
    @ExceptionHandler({UserException.class})
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        log.info("Exception!", exception);
        UserException e = (UserException) exception;
        Map<String, List<String>> messages = new HashMap<>();
        String field = e.getField() != null ? e.getField()
                : "non_field";
        messages.put(field, new ArrayList<>());
        messages.get(field).add(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ExceptionResponse(
                                "validations",
                                messages
                        )
                );
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ExceptionResponse> handleAuthException(Exception exception) {
        log.info("Exception!", exception);
        AuthenticationException e = (AuthenticationException) exception;
        Map<String, List<String>> messages = new HashMap<>();
        String field = e.getField() != null ? e.getField()
                : "non_field";
        messages.put(field, new ArrayList<>());
        messages.get(field).add(e.getMessage());
        return ResponseEntity
                .status(e.getMessage().contains("expired") ? HttpStatus.UNAUTHORIZED : HttpStatus.BAD_REQUEST)
                .body(
                        new ExceptionResponse(
                                "validations",
                                messages
                        )
                );
    }
}
