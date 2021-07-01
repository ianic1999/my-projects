package com.example.demo.controller.advice;

import com.example.demo.controller.AuthenticationController;
import com.example.demo.dto.response.ExceptionResponse;
import com.example.demo.model.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice(basePackageClasses = {AuthenticationController.class})
@Slf4j
public class AuthenticationControllerAdvice {
    @ExceptionHandler({UserException.class, MailParseException.class, MailAuthenticationException.class, MailSendException.class})
    public ResponseEntity<ExceptionResponse> handleUserException(Exception exception) {
        log.info("Exception!", exception);
        UserException e = (UserException) exception;
        Map<String, List<String>> messages = new HashMap<>();
        if (e.getMessages() != null) {
            messages.put("password", e.getMessages());
        }
        else {
            String field = e.getMessage().startsWith("Parol") ? "password" : e.getMessage().startsWith("User") ? "email" : "non_field";
            messages.put(field, new ArrayList<>());
            messages.get(field).add(e.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ExceptionResponse(
                                "validations",
                                messages
                        )
                );
    }
}
