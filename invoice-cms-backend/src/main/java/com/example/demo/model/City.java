package com.example.demo.model;

import com.example.demo.dto.CityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "City name should not be empty")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    public CityDTO toDTO() {
        return CityDTO.builder()
                .id(id)
                .name(name)
                .country(country.toDTO())
                .build();
    }
}
