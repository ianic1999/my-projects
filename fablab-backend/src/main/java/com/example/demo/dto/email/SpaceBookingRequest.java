package com.example.demo.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SpaceBookingRequest {
    private String fullName;
    private String email;
    private String telephoneNumber;
    private String dateTime;
    private String description;
    private String spaceName;
}
