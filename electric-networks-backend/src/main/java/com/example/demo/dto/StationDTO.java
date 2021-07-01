package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StationDTO {
    private long id;
    private String name;
    private Integer nrOfOrder;
}
