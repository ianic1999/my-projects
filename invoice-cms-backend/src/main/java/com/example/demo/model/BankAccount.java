package com.example.demo.model;

import com.example.demo.dto.BankAccountDTO;
import com.example.demo.dto.CityForCustomerDTO;
import com.example.demo.util.StringAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @Convert(converter = StringAttributeConverter.class)
    private String name;

    @Convert(converter = StringAttributeConverter.class)
    @NotEmpty(message = "Account IBAN should not be empty")
    private String iban;

    @Convert(converter = StringAttributeConverter.class)
    @NotEmpty(message = "Account swift/bic should not be empty")
    private String swiftBic;

    @NotEmpty(message = "Account street should not be empty")
    private String street;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    private City city;

    @Convert(converter = StringAttributeConverter.class)
    private String postcode;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    public BankAccountDTO toDTO() {
        return BankAccountDTO.builder()
                .id(id)
                .name(name)
                .iban(iban)
                .swiftBic(swiftBic)
                .street(street)
                .country(country.toDTO())
                .city(
                        CityForCustomerDTO.builder()
                                .id(city.getId())
                                .name(city.getName())
                        .build()
                )
                .company(company.toDTO())
                .postcode(postcode)
                .build();
    }
}
