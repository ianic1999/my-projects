package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class InvoiceDTO {
    private long id;
    private long ordinalNumber;
    private CompanyDTO company;
    private CustomerDTO customer;
    private String status;
    private String notes;
    private LocalDate invoiceAt;
    private LocalDate dueAt;
    private BankAccountDTO bankAccount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSentAt;
    private CurrencyDTO currency;
    private List<InvoiceOfferingDTO> offerings;
    private double total;
}
