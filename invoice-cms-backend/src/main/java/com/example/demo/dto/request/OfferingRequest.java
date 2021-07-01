package com.example.demo.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OfferingRequest {
    private long id;
    private String name;
    private String description;
    private double price;
    private String type;
}
