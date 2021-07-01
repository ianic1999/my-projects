package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ImageWithStylesDTO {
    private List<ImageDTO> image;
}
