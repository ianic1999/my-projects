package com.example.demo.controller.advice;

import com.example.demo.controller.*;
import com.example.demo.dto.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice(basePackageClasses = {
        AlbumController.class,
        ArticleController.class,
        AuthenticationController.class,
        CategoryController.class,
        EventController.class,
        PartnerController.class,
        ServiceController.class,
        SettingController.class,
        SpaceController.class,
        TestimonialController.class,
        UserController.class
})
public class MissingParametersControllerAdvice {

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ExceptionResponse> handleException(MissingServletRequestParameterException exception) {
        Map<String, List<String>> map = new HashMap<>();
        map.put(exception.getParameterName(), Collections.singletonList(StringUtils.capitalize(exception.getParameterName()) + " is required"));
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
