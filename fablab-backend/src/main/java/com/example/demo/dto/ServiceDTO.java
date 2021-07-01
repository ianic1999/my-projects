package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class ServiceDTO {
    private long id;
    private ServiceTitleDTO title;
    private ServiceSummaryDTO summary;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CreatedByDTO createdBy;
}
