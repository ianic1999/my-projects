package com.example.demo.model;

import com.example.demo.dto.AlbumDTO;
import com.example.demo.dto.ImageWithStylesDTO;
import com.example.demo.util.ServerUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Cover Image should not be null")
    @Pattern(regexp = ".+\\.(png|jpg|jpeg|webp|jpeg2000)", message = "Image should be in .png, .jpg, .jpeg, .webp or .jpeg2000 format")
    private String coverImage;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "album", cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

    private boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    public AlbumDTO toDTO() {
        return AlbumDTO.builder()
                .id(id)
                .coverImage(ServerUtil.getHostname() + "/" + coverImage)
                .isPublic(isPublic)
                .images(
                        images.stream().collect(Collectors.groupingBy(
                                image -> image.getFilename().substring(image.getKey().getKey().length())
                        ))
                                .values()
                                .stream()
                                .map(
                                        imageList -> ImageWithStylesDTO.builder()
                                                .image(
                                                        imageList.stream()
                                                                .map(Image::toDTO)
                                                                .collect(Collectors.toList())
                                                )
                                                .build()
                                )
                                .collect(Collectors.toList())
                )
                .createdBy(createdBy.toCreatedByDTO())
                .build();
    }
}
