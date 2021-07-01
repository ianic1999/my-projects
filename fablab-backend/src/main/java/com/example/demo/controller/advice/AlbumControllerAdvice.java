package com.example.demo.controller.advice;

import com.example.demo.controller.AlbumController;
import com.example.demo.dto.response.ExceptionResponse;
import com.example.demo.model.exception.AlbumException;
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

@RestControllerAdvice(basePackageClasses = {AlbumController.class})
@Slf4j
public class AlbumControllerAdvice {
    @ExceptionHandler({AlbumException.class, IOException.class, UserException.class})
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.info("Exception!", e);
        Map<String, List<String>> messages = new HashMap<>();
        messages.put("album", new ArrayList<>());
        messages.get("album").add(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("validations", messages));
    }
}
