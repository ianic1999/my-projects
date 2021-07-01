package com.example.demo.controller.advice;

import com.example.demo.controller.IndicationController;
import com.example.demo.dto.response.ExceptionResponse;
import com.example.demo.model.exception.CustomerException;
import com.example.demo.model.exception.IndicationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice(basePackageClasses = {IndicationController.class})
public class IndicationControllerAdvice {
    @ExceptionHandler({CustomerException.class, IndicationException.class})
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        Map<String, List<String>> messages = new HashMap<>();
        messages.put("non_field", new ArrayList<>());
        messages.get("non_field").add(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("validations", messages));
    }
}
