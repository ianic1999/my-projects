package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EstateDTO {
    private long id;
    private String title;
    private String street;
    private String mapUrl;
    private String type;
    private String service;
    private double price;
    private LocationDTO sector;
    private String constructionType;
    private AgentDTO agent;
    private double squareMeters;
    private List<FacilityDTO> facilities;
    private double ares;
    private String landDestination;
    private String reparationType;
    private String houseType;
    private String floor;
    private String heating;
    private String constructionCompany;
    private int rooms;
    private int bathrooms;
    private int balconies;
    private String description;
    private List<ImageWithStylesDTO> images;
    private LocalDateTime createdAt;
}
