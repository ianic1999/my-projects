package com.example.demo.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class IndicationRequest {
    private long id;
    private LocalDate selectedAt;
    private double amount;
    private long customer;
}
