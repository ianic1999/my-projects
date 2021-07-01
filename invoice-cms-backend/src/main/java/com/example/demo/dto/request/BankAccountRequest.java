package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BankAccountRequest {
    private long id;
    private String name;
    private long companyId;
    private String iban;
    private String swiftBic;
    private String street;
    private long cityId;
    private String postcode;
}
