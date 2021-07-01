package com.example.demo.controller.advice;

import com.example.demo.controller.UserController;
import com.example.demo.dto.response.ExceptionResponse;
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

@RestControllerAdvice(basePackageClasses = {UserController.class})
@Slf4j
public class UserControllerAdvice {
    @ExceptionHandler({UserException.class, IOException.class})
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.info("Exception!", e);
        Map<String, List<String>> messages = new HashMap<>();
        messages.put("non_field", new ArrayList<>());
        messages.get("non_field").add(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("validations", messages));
    }
}
