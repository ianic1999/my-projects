package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InvoiceOfferingDTO {
    private long id;
    private double price;
    private int quantity;
    private String description;
    private OfferingDTO offering;
}
