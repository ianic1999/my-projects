package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class IndicationsPerMonthDTO {
    private int year;
    private int month;
    private double total;
}
