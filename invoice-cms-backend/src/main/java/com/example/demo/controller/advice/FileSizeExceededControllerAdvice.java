package com.example.demo.controller.advice;

import com.example.demo.dto.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class FileSizeExceededControllerAdvice {
    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<ExceptionResponse> handleException(MaxUploadSizeExceededException exc) {
        Map<String, List<String>> map = new HashMap<>();
        map.put("non_field", Collections.singletonList("Your image exceeds 5 MB"));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ExceptionResponse(
                                "validations",
                                map
                        )
                );
    }
}
