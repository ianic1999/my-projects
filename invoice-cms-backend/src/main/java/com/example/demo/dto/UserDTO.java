package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String image;
    private String role;
    private List<CompanyForUserDTO> companies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String provider;
    private String type;
}
