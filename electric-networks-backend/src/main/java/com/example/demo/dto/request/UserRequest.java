package com.example.demo.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRequest {
    long id;
    String name;
    String email;
    String password;
}
