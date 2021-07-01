package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InvoiceStatisticsDTO {
    private double overdue;
    private double paid;
    private double draft;
    private double unpaid;
}
