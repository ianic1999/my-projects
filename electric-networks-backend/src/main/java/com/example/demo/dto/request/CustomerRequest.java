package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerRequest {
    private long id;
    private String contractNumber;
    private String fullName;
    private String address;
    private String meter;
    private String phone;
    private long station;
}
