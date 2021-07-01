package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SpaceDescriptionDTO {
    private String ro;
    private String ru;
    private String en;
}
