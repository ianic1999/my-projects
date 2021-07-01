package com.example.demo.dto.response;

import com.example.demo.dto.ResponsePair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Response <T> {
    T data;
    ExceptionResponse meta;

    public Response(T data) {
        this.data = data;
    }

    public Response(ResponsePair<T> data) {
        this.data = data.getKey();
        if (data.getValidations().size() > 0) {
            Map<String, List<String>> meta = new HashMap<>();
            meta.put("images", data.getValidations());
            this.meta = new ExceptionResponse("validations", meta);
        }
    }
}
