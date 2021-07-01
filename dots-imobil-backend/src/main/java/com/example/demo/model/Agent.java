package com.example.demo.model;

import com.example.demo.dto.AgentDTO;
import com.example.demo.util.ServerUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Agent first name should not be empty")
    @Size(max = 512, message = "Agent first name length should be at most 512 characters")
    private String firstName;

    @NotEmpty(message = "Agent last name should not be empty")
    @Size(max = 512, message = "Agent last name length should be at most 512 characters")
    private String lastName;

    @NotEmpty(message = "Agent phone should not be empty")
    @Size(max = 512, message = "Agent phone length should be at most 512 characters")
    private String phone;

    @NotEmpty(message = "Agent email should not be empty")
    @Email(message = "Agent email is not valid")
    @Size(max = 512, message = "Agent email length should be at most 512 characters")
    private String email;

    @NotEmpty(message = "Agent image should not be empty")
    @Pattern(regexp = ".+\\.(png|jpg|jpeg|webp|jpeg2000)", message = "Image should be in .png, .jpg, .jpeg, .webp or .jpeg2000 format")
    private String image;

    public AgentDTO toDTO() {
        return AgentDTO.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .email(email)
                .image(ServerUtil.getHostname() + "/" + image)
                .build();
    }
}
