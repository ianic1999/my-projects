package com.example.demo.model;

import com.example.demo.dto.CustomerWithIndicationDTO;
import com.example.demo.dto.IndicationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Indication {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    private LocalDate createdAt;

    @NotNull(message = "Selected date should not be null")
    private LocalDate selectedAt;

    @NotNull(message = "Amount should not be null")
    private double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    public IndicationDTO toDTO() {
        return IndicationDTO.builder()
                .id(id)
                .createdAt(createdAt)
                .selectedAt(selectedAt)
                .amount(amount)
                .customer(
                        CustomerWithIndicationDTO.builder()
                        .id(customer.getId())
                        .fullName(customer.getFullName())
                        .build()
                )
                .build();
    }

}
