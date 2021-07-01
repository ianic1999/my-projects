package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerDTO {
    private long id;
    private String contractNumber;
    private String fullName;
    private String address;
    private String meter;
    private String phone;
    private StationDTO station;
    private boolean isCompleted;
    private Double lastIndication;
}
