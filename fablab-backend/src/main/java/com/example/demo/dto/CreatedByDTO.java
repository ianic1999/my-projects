package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreatedByDTO {
    private long id;
    private String fullName;
    private String image;
}
