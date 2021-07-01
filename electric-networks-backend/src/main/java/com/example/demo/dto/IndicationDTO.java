package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class IndicationDTO {
    private long id;
    private LocalDate createdAt;
    private LocalDate selectedAt;
    private double amount;
    private CustomerWithIndicationDTO customer;
}
