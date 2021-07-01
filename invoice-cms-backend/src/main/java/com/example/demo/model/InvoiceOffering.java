package com.example.demo.model;

import com.example.demo.dto.InvoiceOfferingDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InvoiceOffering {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    private double price;

    private int quantity;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Offering offering;

    public InvoiceOfferingDTO toDTO() {
        return InvoiceOfferingDTO.builder()
                .id(id)
                .price(price)
                .quantity(quantity)
                .description(description)
                .offering(offering.toDTO())
                .build();
    }
}
