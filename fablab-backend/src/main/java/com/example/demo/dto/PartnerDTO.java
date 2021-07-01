package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class PartnerDTO {
    private long id;
    private String name;
    private String image;
    private String link;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CreatedByDTO createdBy;
}
