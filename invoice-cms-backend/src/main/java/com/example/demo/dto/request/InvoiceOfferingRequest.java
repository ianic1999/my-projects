package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceOfferingRequest {
    private long id;
    private long offeringId;
    private double price;
    private int quantity;
    private String description;
}
