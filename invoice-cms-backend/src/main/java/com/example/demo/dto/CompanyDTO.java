package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CompanyDTO {
    private long id;
    private String name;
    private List<BankAccountForCompanyDTO> bankAccounts;
    private CountryDTO country;
    private String image;
    private CityForCustomerDTO city;
    private String street;
    private String state;
    private String zipCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
