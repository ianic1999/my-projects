package com.example.demo.service;

import com.example.demo.dto.EstateDTO;
import com.example.demo.dto.response.ResponsePair;
import com.example.demo.model.exception.AgentException;
import com.example.demo.model.exception.EstateException;
import com.example.demo.model.exception.FacilityException;
import com.example.demo.model.exception.LocationException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface EstateService {
    Page<EstateDTO> get(int page, int perPage, String search, int rooms, int bathrooms, String type, long sectorId, String service, String priceRange) throws EstateException;
    EstateDTO getById(long id) throws EstateException;
    ResponsePair<EstateDTO> add(String type,
                     String title,
                     String street,
                     String mapUrl,
                     String service,
                     double price,
                     long sectorId,
                     String constructionType,
                     long agentId,
                     double squareMeters,
                     double ares,
                     String landDestination,
                     String reparationType,
                     String houseType,
                     String floor,
                     String heating,
                     String constructionCompany,
                     int rooms,
                     int bathrooms,
                     int balconies,
                     String description,
                     long[] facilities,
                     MultipartFile[] images) throws LocationException, AgentException, FacilityException, IOException, EstateException;
    ResponsePair<EstateDTO> update(long id,
                     String title,
                     String street,
                     String mapUrl,
                     String type,
                     String service,
                     double price,
                     long sectorId,
                     String constructionType,
                     long agentId,
                     double squareMeters,
                     double ares,
                     String landDestination,
                     String reparationType,
                     String houseType,
                     String floor,
                     String heating,
                     String constructionCompany,
                     int rooms,
                     int bathrooms,
                     int balconies,
                     String description,
                     long[] facilities,
                     MultipartFile[] images,
                     long[] deletedImages) throws EstateException, AgentException, LocationException, FacilityException, IOException;
    void remove(long id) throws EstateException, IOException;
}
