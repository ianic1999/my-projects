package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class CategoryDTO {
    private long id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
