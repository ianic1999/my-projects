package com.example.demo.model;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.util.AWSUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Article title should not be empty")
    @Size(max = 512, message = "Article keywords length should be at most 512 characters")
    private String title;

    @NotEmpty(message = "Article title should not be empty")
    private String description;

    @NotEmpty(message = "Article body should not be empty")
    @Column(columnDefinition = "TEXT")
    private String body;

    @NotEmpty(message = "Article keywords should not be empty")
    @Size(max = 2048, message = "Article keywords length should be at most 2014 characters")
    private String keywords;

    @NotEmpty(message = "Article image should not be empty")
    @Pattern(regexp = ".+\\.(png|jpg|jpeg)", message = "Image should be in .png, .jpg or .jpeg format")
    private String image;

    @NotEmpty(message = "Alis should not be empty")
    private String alias;

    private String youtubeLink;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ArticleDTO toDTO() {
        return ArticleDTO.builder()
                .id(id)
                .title(title)
                .description(description)
                .body(body)
                .image(AWSUtil.getURL() + "articles/" + id + "/" + image)
                .keywords(keywords)
                .youtubeLink(youtubeLink)
                .alias(alias)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
