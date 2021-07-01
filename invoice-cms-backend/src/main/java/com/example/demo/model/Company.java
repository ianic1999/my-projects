package com.example.demo.model;

import com.example.demo.dto.BankAccountForCompanyDTO;
import com.example.demo.dto.CityForCustomerDTO;
import com.example.demo.dto.CompanyDTO;
import com.example.demo.util.AWSUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
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
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Company name should not be null or empty")
    @Size(max = 256, message = "Company name should have at most 256 characters")
    private String name;

    @Pattern(regexp = ".+\\.(png|jpg|jpeg)", message = "Image should be in .png, .jpg or .jpeg format")
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company", cascade = CascadeType.ALL)
    private List<BankAccount> accounts;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    private City city;

    @NotEmpty(message = "Customer street should not be empty")
    @Size(max = 512, message = "Customer street should have at most 512 characters")
    private String street;

    @Size(max = 256, message = "Customer state should have at most 256 characters")
    private String state;

    private String zipCode;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public CompanyDTO toDTO() {
        return CompanyDTO.builder()
                .id(id)
                .name(name)
                .bankAccounts(
                        accounts.stream()
                                .map(
                                        account -> new BankAccountForCompanyDTO(
                                                account.getId(),
                                                account.getName(),
                                                account.getIban(),
                                                account.getSwiftBic(),
                                                account.getStreet(),
                                                account.getCountry().toDTO(),
                                                new CityForCustomerDTO(account.getCity().getId(), account.getCity().getName()),
                                                account.getPostcode())
                                        )
                        .collect(Collectors.toList())
                )
                .image(image != null ? AWSUtil.getURL() + "companies/" + id + "/" + image : null)
                .country(country != null ? country.toDTO() : null)
                .city(city != null ?
                        CityForCustomerDTO.builder()
                                .id(city.getId())
                                .name(city.getName())
                                .build()
                                : null
                )
                .state(state)
                .street(street)
                .zipCode(zipCode)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
