package com.example.demo.model;

import com.example.demo.dto.CreatedByDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.util.ServerUtil;
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
import java.util.Collection;
import java.util.Collections;

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

    @Column(unique = true)
    @NotEmpty(message = "Email should not be null or empty")
    @Email(message = "Email is not valid")
    private String email;

    @NotEmpty
    private String fullName;

    @NotEmpty(message = "Image should not be null")
    @Pattern(regexp = ".+\\.(png|jpg|jpeg|webp|jpeg2000)", message = "Image should be in .png, .jpg, .jpeg, .webp or .jpeg2000 format")
    private String image;

    @NotEmpty(message = "Password should not be null or empty")
    private String password;

    @Builder.Default
    private UserRole role = UserRole.USER;

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
                .email(email)
                .fullName(fullName)
                .image(ServerUtil.getHostname() + "/" + image)
                .role(role.getKey())
                .build();
    }

    public CreatedByDTO toCreatedByDTO() {
        return CreatedByDTO.builder()
                .id(id)
                .fullName(fullName)
                .image(ServerUtil.getHostname() + "/" + image)
                .build();
    }
}
