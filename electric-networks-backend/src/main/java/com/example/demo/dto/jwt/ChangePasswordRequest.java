package com.example.demo.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    private String password;
    private String confirmPassword;
    private String currentPassword;
}
