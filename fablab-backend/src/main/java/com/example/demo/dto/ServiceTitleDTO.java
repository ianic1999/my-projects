package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ServiceTitleDTO {
    private String ro;
    private String ru;
    private String en;
}
