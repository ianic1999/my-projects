package com.example.demo.model;

import com.example.demo.dto.SpaceImageDTO;
import com.example.demo.util.ServerUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpaceImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Filename should not be null")
    private String filename;

    @NotEmpty(message = "Image should not be null")
    @Pattern(regexp = ".+\\.(png|jpg|jpeg|webp|jpeg2000)", message = "Image should be in .png, .jpg, .jpeg, .webp or .jpeg2000 format")
    private String path;

    @ManyToOne
    private Space space;

    public SpaceImageDTO toDTO() {
        return SpaceImageDTO.builder()
                .id(id)
                .filename(filename)
                .path(ServerUtil.getHostname() + "/" + path)
                .build();
    }
}
