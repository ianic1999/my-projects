package com.example.demo.dto.request;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class InvoiceRequest {
    private long id;
    private long ordinalNumber;
    private long companyId;
    private long customerId;
    private long bankAccountId;
    private String status;
    private String notes;
    private LocalDate invoiceAt;
    private LocalDate dueAt;
    private String currency;
    private List<InvoiceOfferingRequest> offerings;
    private List<Long> deletedOfferings;
}
