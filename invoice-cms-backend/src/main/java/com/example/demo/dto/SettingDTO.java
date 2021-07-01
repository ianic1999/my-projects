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
public class SettingDTO {
    private long id;
    private String key;
    private boolean isPublic;
    private SettingValueDTO value;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
