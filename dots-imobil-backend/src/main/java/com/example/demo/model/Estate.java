package com.example.demo.model;

import com.example.demo.dto.EstateDTO;
import com.example.demo.dto.ImageWithStylesDTO;
import com.example.demo.model.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Estate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    private String title;

    private String street;

    private String mapUrl;

    @NotNull(message = "Estate type should not be null")
    private EstateType type;

    @NotNull(message = "Estate service should not be null")
    private ServiceType service;

    private double price;

    @NotNull(message = "Estate sector should not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    private Location sector;

    @NotNull(message = "Estate construction type should not be null")
    private ConstructionType constructionType;

    @NotNull(message = "Estate agent should not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    private Agent agent;

    private double squareMeters;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "estate", cascade = CascadeType.ALL)
    private List<EstateImage> images;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "estate", cascade = CascadeType.ALL)
    private List<EstateFacility> facilities;

    private double ares;

    private LandDestinationType landDestination;

    private ReparationType reparationType;

    private HouseType houseType;

    @Size(max = 512, message = "Estate floor length should be at most 512 characters")
    private String floor;

    private HeatingType heating;

    @Size(max = 512, message = "Estate construction company length should be at most 512 characters")
    private String constructionCompany;

    private int rooms;

    private int bathrooms;

    private int balconies;

    private LocalDateTime createdAt;

    @Size(max = 4096, message = "Estate construction company length should be at most 4096 characters")
    private String description;

    public EstateDTO toDTO() {
        return EstateDTO.builder()
                .id(id)
                .title(title)
                .street(street)
                .mapUrl(mapUrl)
                .type(type.getKey())
                .service(service.getKey())
                .price(price)
                .sector(sector.toDTO())
                .constructionType(constructionType.getKey())
                .agent(agent.toDTO())
                .squareMeters(squareMeters)
                .facilities(
                        facilities.stream().map(EstateFacility::getFacility).map(Facility::toDTO).collect(Collectors.toList())
                )
                .ares(ares)
                .landDestination(landDestination == null ? null : landDestination.getKey())
                .reparationType(reparationType == null ? null : reparationType.getKey())
                .houseType(houseType == null ? null : houseType.getKey())
                .floor(floor)
                .heating(heating == null ? null : heating.getKey())
                .constructionCompany(constructionCompany)
                .rooms(rooms)
                .bathrooms(bathrooms)
                .balconies(balconies)
                .description(description)
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
                                                                .map(EstateImage::toDTO)
                                                                .collect(Collectors.toList())
                                                )
                                                .build()
                                )
                                .collect(Collectors.toList())
                )
                .createdAt(createdAt)
                .build();
    }

}
