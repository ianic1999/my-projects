package com.example.demo.controller.advice;

import com.example.demo.controller.ArticleController;
import com.example.demo.dto.response.ExceptionResponse;
import com.example.demo.model.exception.ArticleException;
import com.example.demo.model.exception.CategoryException;
import com.example.demo.model.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.*;

@RestControllerAdvice(basePackageClasses = {ArticleController.class})
@Slf4j
public class ArticleControllerAdvice {
    @ExceptionHandler({ArticleException.class, CategoryException.class, IOException.class, UserException.class})
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
