package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerWithIndicationDTO {
    private long id;
    private String fullName;
}
