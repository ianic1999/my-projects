package com.example.demo.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangePasswordRequest {
    private String password;
    private String confirmPassword;
    private String currentPassword;
}
