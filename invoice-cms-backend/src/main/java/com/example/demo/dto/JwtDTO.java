package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JwtDTO {
    private String accessToken;
    private String refreshToken;
}
