package com.example.demo.model;

import com.example.demo.dto.BankAccountDTO;
import com.example.demo.dto.CompanyDTO;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.InvoiceDTO;
import com.example.demo.model.enums.Currency;
import com.example.demo.model.enums.InvoiceStatus;
import com.example.demo.util.StringAttributeConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long ordinalNumber;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceOffering> offerings;

    @NotEmpty(message = "Customer should not be null")
    @Column(columnDefinition = "TEXT")
    private String customer;

    @Column(columnDefinition = "TEXT")
    private String company;

    @NotEmpty(message = "Bank account should not be null")
    @Convert(converter = StringAttributeConverter.class)
    @Column(columnDefinition = "TEXT")
    private String bankAccount;

    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Size(max = 1024, message = "Invoice notes should have at most 1024 characters")
    private String notes;

    @NotNull(message = "Invoice date should not be null")
    private LocalDate invoiceAt;

    @NotNull(message = "Invoice due at date should not be null")
    private LocalDate dueAt;

    private double total;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastSentAt;

    private Currency currency;

    @Column(columnDefinition = "TEXT")
    private String owner;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @SneakyThrows
    public InvoiceDTO toDTO() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return InvoiceDTO.builder()
                .id(id)
                .ordinalNumber(ordinalNumber)
                .customer(customer != null ? mapper.readValue(customer, CustomerDTO.class) : null)
                .company(company != null ? mapper.readValue(company, CompanyDTO.class) : null)
                .notes(notes)
                .status(status.getKey())
                .invoiceAt(invoiceAt)
                .dueAt(dueAt)
                .bankAccount(bankAccount != null ? mapper.readValue(bankAccount, BankAccountDTO.class) : null)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastSentAt(lastSentAt)
                .currency(currency != null ? currency.toDTO(): null)
                .offerings(
                        offerings.stream()
                                .map(InvoiceOffering::toDTO)
                                .collect(Collectors.toList())
                )
                .total(total)
                .build();
    }

}
