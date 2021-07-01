package com.example.demo.model;

import com.example.demo.dto.EstateImageDTO;
import com.example.demo.util.ImageKey;
import com.example.demo.util.ServerUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EstateImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Image should not be null")
    @Pattern(regexp = ".+\\.(png|jpg|jpeg|webp|jpeg2000)", message = "Image should be in .png, .jpg, .jpeg, .webp or .jpeg2000 format")
    private String filename;

    @NotNull
    private ImageKey key;

    @NotEmpty(message = "Image should not be null")
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    private Estate estate;

    public EstateImageDTO toDTO() {
        return EstateImageDTO.builder()
                .id(id)
                .filename(filename)
                .key(key.getKey())
                .path(ServerUtil.getHostname() + "/" + path)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EstateImage image = (EstateImage) o;
        return id == image.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
