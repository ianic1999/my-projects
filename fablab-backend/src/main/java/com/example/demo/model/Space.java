package com.example.demo.model;

import com.example.demo.dto.SpaceDTO;
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
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotNull(message = "Title should not be null")
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "space", cascade = CascadeType.ALL)
    private SpaceTitle title;

    @NotNull(message = "Area should not be null")
    @PositiveOrZero(message = "Area should be positive")
    private double area;

    @NotNull(message = "Price should not be null")
    @PositiveOrZero(message = "Price should be positive")
    private double price;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL)
    private List<SpaceImage> images;

    @NotNull(message = "Description should not be null")
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "space", cascade = CascadeType.ALL)
    private SpaceDescription description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    public SpaceDTO toDTO() {
        return SpaceDTO.builder()
                .id(id)
                .title(title.toDTO())
                .description(description.toDTO())
                .area(area)
                .price(price)
                .images(images.stream().map(SpaceImage::toDTO).collect(Collectors.toList()))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .createdBy(createdBy.toCreatedByDTO())
                .build();
    }
}
