package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDTO {
    private long id;
    private String name;
    private String email;
    private String role;
}
