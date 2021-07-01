package com.example.demo.model;

import com.example.demo.dto.CityForCustomerDTO;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.util.AWSUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Customer first name should not be empty")
    @Size(max = 256, message = "Customer first name should have at most 256 characters")
    private String firstName;

    @NotEmpty(message = "Customer last name should not be empty")
    @Size(max = 256, message = "Customer last name should have at most 256 characters")
    private String lastName;

    @NotEmpty(message = "Customer email should not be empty")
    @Email(message = "Customer email is not valid")
    @Size(max = 20000, message = "Customer email should have at most 20000 characters")
    private String email;

    @Pattern(regexp = ".+\\.(png|jpg|jpeg)", message = "Image should be in .png, .jpg or .jpeg format")
    private String image;

    @Size(max = 256, message = "Company name should have at most 256 characters")
    private String companyName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    private City city;

    @NotEmpty(message = "Customer street should not be empty")
    @Size(max = 512, message = "Customer street should have at most 512 characters")
    private String street;

    @Size(max = 256, message = "Customer state should have at most 256 characters")
    private String state;

    private String zipCode;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    public CustomerDTO toDTO() {
        return CustomerDTO.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .image(image != null ? AWSUtil.getURL() + "customers/" + id + "/" + image : null)
                .companyName(companyName)
                .country(country != null ? country.toDTO() : null)
                .city(city != null ?
                        CityForCustomerDTO.builder()
                        .id(city.getId())
                        .name(city.getName())
                        .build() : null
                )
                .street(street)
                .state(state)
                .zipCode(zipCode)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
