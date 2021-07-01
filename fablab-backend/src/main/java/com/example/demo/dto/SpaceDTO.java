package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class SpaceDTO {
    private long id;
    private SpaceTitleDTO title;
    private SpaceDescriptionDTO description;
    private double area;
    private double price;
    private List<SpaceImageDTO> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CreatedByDTO createdBy;
}
