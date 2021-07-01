package com.example.demo.model;

import com.example.demo.dto.CityForCustomerDTO;
import com.example.demo.dto.CompanyForUserDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.enums.UserProvider;
import com.example.demo.model.enums.UserRole;
import com.example.demo.model.enums.UserType;
import com.example.demo.util.AWSUtil;
import com.example.demo.util.StringAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @Convert(converter = StringAttributeConverter.class)
    @Size(max = 256, message = "User first name should have at most 256 characters")
    private String firstName;

    @Convert(converter = StringAttributeConverter.class)
    @Size(max = 256, message = "User last name should have at most 256 characters")
    private String lastName;

    @Convert(converter = StringAttributeConverter.class)
    @Email(message = "User email is not valid")
    @NotEmpty(message = "User email should not be empty")
    @Size(max = 20000, message = "User email should have at most 20000 characters")
    private String email;

    @Pattern(regexp = ".+\\.(png|jpg|jpeg)", message = "Image should be in .png, .jpg or .jpeg format")
    private String image;

    @NotEmpty(message = "Password should not be empty")
    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder.Default
    private UserRole role = UserRole.USER;

    @Builder.Default
    private UserProvider provider = UserProvider.LOCAL;

    @Builder.Default
    private UserType type = UserType.FREELANCE;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Company> companies;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Customer> customers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Invoice> invoices;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Offering> offerings;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(role.getKey())
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserDTO toDTO() {
        return UserDTO.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .role(role.getKey())
                .provider(provider != null ? provider.getKey() : UserProvider.LOCAL.getKey())
                .type(type != null ? type.getKey() : UserType.FREELANCE.getKey())
                .image(image != null ? AWSUtil.getURL() + "users/" + id + "/" + image : null)
                .companies(companies.stream().map(
                        company -> CompanyForUserDTO.builder()
                            .id(company.getId())
                            .name(company.getName())
                            .country(company.getCountry() != null ? company.getCountry().toDTO() : null)
                            .city(company.getCity() != null ?
                                    CityForCustomerDTO.builder()
                                        .id(company.getCity().getId())
                                        .name(company.getCity().getName())
                                    .build() : null
                            )
                            .state(company.getState())
                            .street(company.getStreet())
                            .image(company.getImage() != null ? AWSUtil.getURL() + "companies/" + company.getId() + "/" + image : null)
                            .createdAt(company.getCreatedAt())
                            .updatedAt(company.getUpdatedAt())
                            .zipCode(company.getZipCode())
                            .build()

                        )
                    .collect(Collectors.toList()))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
