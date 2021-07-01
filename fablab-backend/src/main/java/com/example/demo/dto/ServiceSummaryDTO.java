package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ServiceSummaryDTO {
    private String ro;
    private String ru;
    private String en;
}
