package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ArticleDTO {
    private long id;
    private String title;
    private String description;
    private String body;
    private String keywords;
    private String image;
    private String youtubeLink;
    private String alias;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
