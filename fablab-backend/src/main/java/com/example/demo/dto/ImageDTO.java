package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ImageDTO {
    private long id;
    private String filename;
    private String key;
    private String path;
}
