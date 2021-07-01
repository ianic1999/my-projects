package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class ArticleDTO {
    private long id;
    private String title;
    private String body;
    private String image;
    private CategoryDTO category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CreatedByDTO createdBy;
}
