package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AgentDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String image;
}
