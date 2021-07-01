package com.example.demo.model;

import com.example.demo.dto.FacilityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Facility name should not be empty")
    @Size(max = 512, message = "Facility name length should be at most 512 characters")
    private String name;

    @NotEmpty(message = "Facility icon should not be empty")
    @Size(max = 512, message = "Facility icon length should be at most 512 characters")
    private String icon;

    @Size(max = 512, message = "Facility icon length should be at most 2048 characters")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facility", cascade = CascadeType.ALL)
    private List<EstateFacility> estates;

    public FacilityDTO toDTO() {
        return FacilityDTO.builder()
                .id(id)
                .name(name)
                .icon(icon)
                .description(description)
                .build();
    }
}
