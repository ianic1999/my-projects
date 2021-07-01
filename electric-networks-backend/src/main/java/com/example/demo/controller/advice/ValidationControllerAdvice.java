package com.example.demo.controller.advice;

import com.example.demo.controller.*;
import com.example.demo.dto.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackageClasses = {
        AuthenticationController.class,
        StationController.class,
        CustomerController.class,
        IndicationController.class,
        UserController.class
})
@Slf4j
public class ValidationControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler({TransactionSystemException.class})
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(Exception exception, WebRequest request) {
        if (((TransactionSystemException) exception).getRootCause() instanceof ConstraintViolationException) {
            ConstraintViolationException e = (ConstraintViolationException) ((TransactionSystemException)exception).getRootCause();
            log.info("Exception!", e);
            Map<String, List<String>> messages = e.getConstraintViolations().stream()
                    .collect(Collectors.groupingBy(constraint -> constraint.getPropertyPath().toString(), Collectors.toList()))
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList())));
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ExceptionResponse(
                                    "validations",
                                    messages
                            )
                    );
        }
        return null;
    }
}
