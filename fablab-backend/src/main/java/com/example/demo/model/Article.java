package com.example.demo.model;

import com.example.demo.dto.ArticleDTO;
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
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Title should not be null or empty")
    @Size(max = 256, message = "Title should have at most 256 characters")
    private String title;

    @NotEmpty(message = "Body should not be null or empty")
    @Size(max = 20000, message = "Body should have at most 20000 characters")
    private String body;

    @NotEmpty(message = "Image should not be null")
    @Pattern(regexp = ".+\\.(png|jpg|jpeg|webp|jpeg2000)", message = "Image should be in .png, .jpg, .jpeg, .webp or .jpeg2000 format")
    private String image;

    @NotNull(message = "Category should not be null")
    @OneToOne(fetch = FetchType.LAZY)
    private Category category;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    public ArticleDTO toDTO() {
        return ArticleDTO.builder()
                .id(id)
                .title(title)
                .body(body)
                .image(ServerUtil.getHostname() + "/" + image)
                .category(category.toDTO())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .createdBy(createdBy.toCreatedByDTO())
                .build();
    }
}
