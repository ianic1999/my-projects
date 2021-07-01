package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AlbumDTO {
    private long id;
    private String coverImage;
    private List<ImageWithStylesDTO> images;
    private boolean isPublic;
    private CreatedByDTO createdBy;
}
