package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StationWithCustomersDTO {
    private long id;
    private String name;
    private Integer nrOfOrder;
    private int customers;
    private int indications;
}
