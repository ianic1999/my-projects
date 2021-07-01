package com.example.demo.model;

import com.example.demo.dto.CategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Title should not be null or empty")
    @Size(max = 256, message = "Title should have at most 256 characters")
    private String title;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public CategoryDTO toDTO() {
        return CategoryDTO.builder()
                .id(id)
                .title(title)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
