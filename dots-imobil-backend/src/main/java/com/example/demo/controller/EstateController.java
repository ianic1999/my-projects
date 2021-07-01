package com.example.demo.controller;

import com.example.demo.dto.EstateDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.AgentException;
import com.example.demo.model.exception.EstateException;
import com.example.demo.model.exception.FacilityException;
import com.example.demo.model.exception.LocationException;
import com.example.demo.service.EstateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/estates")
@CrossOrigin("*")
@RequiredArgsConstructor
public class EstateController {
    private final EstateService estateService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<EstateDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "15") int perPage,
                                                               @RequestParam(defaultValue = "") String search,
                                                               @RequestParam(defaultValue = "-1") int rooms,
                                                               @RequestParam(defaultValue = "-1") int bathrooms,
                                                               @RequestParam(required = false) String type,
                                                               @RequestParam(defaultValue = "-1") long sector,
                                                               @RequestParam(required = false) String service,
                                                               @RequestParam(required = false, name = "price__range") String priceRange) throws EstateException {
        return ResponseEntity.ok(
                new PaginatedResponse<>(estateService.get(page, perPage, search, rooms, bathrooms, type, sector, service, priceRange))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<EstateDTO>> getById(@PathVariable long id) throws EstateException {
        return ResponseEntity.ok(
                new Response<>(estateService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<EstateDTO>> add(@RequestParam String type,
                                                   @RequestParam String title,
                                                   @RequestParam String street,
                                                   @RequestParam(required = false) String mapUrl,
                                                   @RequestParam String service,
                                                   @RequestParam(defaultValue = "0.0") double price,
                                                   @RequestParam long sectorId,
                                                   @RequestParam String constructionType,
                                                   @RequestParam long agentId,
                                                   @RequestParam(defaultValue = "0.0") double squareMeters,
                                                   @RequestParam(defaultValue = "0.0") double ares,
                                                   @RequestParam(required = false) String landDestination,
                                                   @RequestParam(required = false) String reparationType,
                                                   @RequestParam(required = false) String houseType,
                                                   @RequestParam(required = false) String floor,
                                                   @RequestParam(required = false) String heating,
                                                   @RequestParam(required = false) String constructionCompany,
                                                   @RequestParam(defaultValue = "0") int rooms,
                                                   @RequestParam(defaultValue = "0") int bathrooms,
                                                   @RequestParam(defaultValue = "0") int balconies,
                                                   @RequestParam(required = false) String description,
                                                   @RequestParam(required = false) long[] facilities,
                                                   @RequestParam MultipartFile[] images) throws FacilityException, LocationException, AgentException, IOException, EstateException {
        return new ResponseEntity<>(
                new Response<>(estateService.add(type, title, street, mapUrl, service, price, sectorId, constructionType, agentId, squareMeters, ares, landDestination, reparationType, houseType, floor, heating, constructionCompany, rooms, bathrooms, balconies, description, facilities, images)),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{estateId}")
    public ResponseEntity<Response<EstateDTO>> update(@PathVariable long estateId,
                                                      @RequestParam long id,
                                                      @RequestParam(required = false) String title,
                                                      @RequestParam(required = false) String street,
                                                      @RequestParam(required = false) String mapUrl,
                                                      @RequestParam(required = false) String type,
                                                      @RequestParam(required = false) String service,
                                                      @RequestParam(defaultValue = "0.0") double price,
                                                      @RequestParam(defaultValue = "0") long sectorId,
                                                      @RequestParam(required = false) String constructionType,
                                                      @RequestParam(defaultValue = "0") long agentId,
                                                      @RequestParam(defaultValue = "0.0") double squareMeters,
                                                      @RequestParam(defaultValue = "0.0") double ares,
                                                      @RequestParam(required = false) String landDestination,
                                                      @RequestParam(required = false) String reparationType,
                                                      @RequestParam(required = false) String houseType,
                                                      @RequestParam(required = false) String floor,
                                                      @RequestParam(required = false) String heating,
                                                      @RequestParam(required = false) String constructionCompany,
                                                      @RequestParam(defaultValue = "0") int rooms,
                                                      @RequestParam(defaultValue = "0") int bathrooms,
                                                      @RequestParam(defaultValue = "0") int balconies,
                                                      @RequestParam(required = false) String description,
                                                      @RequestParam(required = false) long[] facilities,
                                                      @RequestParam(required = false) MultipartFile[] images,
                                                      @RequestParam(required = false) long[] deletedImages) throws IOException, FacilityException, LocationException, AgentException, EstateException {
        return ResponseEntity.ok(
                new Response<>(estateService.update(estateId, title, street, mapUrl, type, service, price, sectorId, constructionType, agentId, squareMeters, ares, landDestination, reparationType, houseType, floor, heating, constructionCompany, rooms, bathrooms, balconies, description, facilities, images, deletedImages))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws IOException, EstateException {
        estateService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
