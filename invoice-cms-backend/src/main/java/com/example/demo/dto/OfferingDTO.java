package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OfferingDTO {
    private long id;
    private String name;
    private String description;
    private double price;
    private String type;
}
