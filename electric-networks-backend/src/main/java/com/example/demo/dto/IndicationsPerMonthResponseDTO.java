package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class IndicationsPerMonthResponseDTO {
    private List<IndicationsPerMonthDTO> months;
}
