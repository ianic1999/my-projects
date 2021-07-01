package com.example.demo.controller.advice;

import com.example.demo.controller.OfferingController;
import com.example.demo.dto.response.ExceptionResponse;
import com.example.demo.model.exception.InvoiceException;
import com.example.demo.model.exception.OfferingException;
import com.example.demo.model.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice(basePackageClasses = {OfferingController.class})
@Slf4j
public class OfferingControllerAdvice {
    @ExceptionHandler({OfferingException.class, InvoiceException.class, UserException.class})
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        log.info("Exception!", exception);
        Map<String, List<String>> messages = new HashMap<>();
        messages.put("non_field", new ArrayList<>());
        messages.get("non_field").add(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("validations", messages));
    }
}
