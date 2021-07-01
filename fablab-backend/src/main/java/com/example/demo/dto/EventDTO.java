package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class EventDTO {
    private long id;
    private String title;
    private String body;
    private String location;
    private LocalDateTime dueDateAt;
    private String link;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CreatedByDTO createdBy;
}
