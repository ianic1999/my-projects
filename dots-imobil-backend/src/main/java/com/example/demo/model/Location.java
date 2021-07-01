package com.example.demo.model;

import com.example.demo.dto.LocationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotNull(message = "Location title should not be empty")
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "location", cascade = CascadeType.ALL)
    private LocationTitle title;

    public LocationDTO toDTO() {
        return LocationDTO.builder()
                .id(id)
                .title(title.toDTO())
                .build();
    }
}
