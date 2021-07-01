package com.example.demo.dto;

import com.example.demo.model.SettingValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SettingValueDTO {
    private long id;
    private String ro;
    private String en;
    private String ru;
}
