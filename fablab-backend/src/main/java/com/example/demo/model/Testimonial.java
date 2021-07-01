package com.example.demo.model;

import com.example.demo.dto.TestimonialDTO;
import com.example.demo.util.ServerUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Testimonial {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Name should not be null or empty")
    @Size(max = 256, message = "Name should have at most 256 characters")
    private String name;

    @NotEmpty(message = "Job Title should not be null or empty")
    @Size(max = 512, message = "Job Title should have at most 512 characters")
    private String jobTitle;

    @NotEmpty(message = "Image should not be null")
    @Pattern(regexp = ".+\\.(png|jpg|jpeg|webp|jpeg2000)", message = "Image should be in .png, .jpg, .jpeg, .webp or .jpeg2000 format")
    private String image;

    @NotEmpty(message = "Message should not be null or empty")
    @Size(max = 10000, message = "Message should have at most 10000 characters")
    private String message;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    public TestimonialDTO toDTO() {
        return TestimonialDTO.builder()
                .id(id)
                .jobTitle(jobTitle)
                .image(ServerUtil.getHostname() + "/" + image)
                .message(message)
                .name(name)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .createdBy(createdBy.toCreatedByDTO())
                .build();
    }
}
