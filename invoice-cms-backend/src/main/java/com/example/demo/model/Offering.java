package com.example.demo.model;

import com.example.demo.dto.OfferingDTO;
import com.example.demo.model.enums.OfferingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Offering {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Offering name should not be empty")
    @Size(max = 256, message = "Offering name should have at most 256 characters")
    private String name;

    @Size(max = 200, message = "Offering description should have at most 200 characters")
    private String description;

    @NotNull(message = "Offering price should not be null")
    private double price;

    @NotNull(message = "Offering type should not be null")
    private OfferingType type;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "offering", cascade = CascadeType.ALL)
    private List<InvoiceOffering> invoices;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    public OfferingDTO toDTO() {
        return OfferingDTO.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .type(type.getKey())
                .build();
    }
}
