package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String image;
    private String companyName;
    private CountryDTO country;
    private CityForCustomerDTO city;
    private String street;
    private String state;
    private String zipCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
