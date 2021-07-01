package com.example.demo.service.impl;

import com.example.demo.dto.EstateDTO;
import com.example.demo.dto.response.ResponsePair;
import com.example.demo.model.*;
import com.example.demo.model.enums.*;
import com.example.demo.model.exception.AgentException;
import com.example.demo.model.exception.EstateException;
import com.example.demo.model.exception.FacilityException;
import com.example.demo.model.exception.LocationException;
import com.example.demo.repository.*;
import com.example.demo.service.EstateService;
import com.example.demo.util.FileSaver;
import com.example.demo.util.ImageKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstateServiceImpl implements EstateService {
    private final EstateRepository estateRepository;
    private final LocationRepository locationRepository;
    private final EstateImageRepository estateImageRepository;
    private final AgentRepository agentRepository;
    private final FacilityRepository facilityRepository;
    private final EstateFacilityRepository estateFacilityRepository;
    private final EntityManager entityManager;

    @Override
    public Page<EstateDTO> get(int page, int perPage, String search, int rooms, int bathrooms, String type, long sectorId, String service, String priceRange) throws EstateException {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Estate> query = builder.createQuery(Estate.class);
        Root<Estate> root = query.from(Estate.class);
        Predicate predicateForStreet = builder.like(builder.lower(root.get("street")), "%" + search.toLowerCase() + "%");
        Predicate predicateForTitle = builder.like(builder.lower(root.get("title")), "%" + search.toLowerCase() + "%");
        Predicate predicateForDescription = builder.like(builder.lower(root.get("description")), "%" + search.toLowerCase() + "%");
        Predicate predicateGlobalSearch = builder.or(predicateForStreet, predicateForDescription, predicateForTitle);
        Predicate predicateRooms = rooms > 0 ? builder.equal(root.get("rooms"), rooms) : null;
        Predicate predicateBathrooms = bathrooms > 0 ? builder.equal(root.get("bathrooms"), bathrooms) : null;
        Predicate predicateSector = sectorId > 0 ? builder.equal(root.get("sector").get("id"), sectorId) : null;
        Predicate predicateType = type != null ? builder.equal(root.get("type"), getEstateTypeFromString(type)) : null;
        Predicate predicateService = service != null ? builder.equal(root.get("service"), getServiceTypeFromString(service)) : null;
        Predicate predicatePriceRange = priceRange != null ? builder.between(root.get("price"), Double.valueOf(priceRange.split(",")[0]), Double.valueOf(priceRange.split(",")[1])) : null;
        if (predicateRooms != null)
            predicateGlobalSearch = builder.and(predicateGlobalSearch, predicateRooms);
        if (predicateBathrooms != null)
            predicateGlobalSearch = builder.and(predicateGlobalSearch, predicateBathrooms);
        if (predicateSector != null)
            predicateGlobalSearch = builder.and(predicateGlobalSearch, predicateSector);
        if (predicateType != null)
            predicateGlobalSearch = builder.and(predicateGlobalSearch, predicateType);
        if (predicateService != null)
            predicateGlobalSearch = builder.and(predicateGlobalSearch, predicateService);
        if (predicatePriceRange != null)
            predicateGlobalSearch = builder.and(predicateGlobalSearch, predicatePriceRange);
        query.where(predicateGlobalSearch);
        List<Estate> estates = entityManager.createQuery(query.select(root)).getResultList();

        int total = estates.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);

        List<Estate> output = new ArrayList<>();

        if (start <= end) {
            output = estates.subList(start, end);
        }
        return new PageImpl<>(output, pageable, total).map(Estate::toDTO);
    }

    @Override
    public EstateDTO getById(long id) throws EstateException {
        return estateRepository.findById(id).orElseThrow(() -> new EstateException("Estate with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    @Transactional
    public ResponsePair<EstateDTO> add(String type, String title, String street, String mapUrl, String service, double price, long sectorId, String constructionType, long agentId, double squareMeters, double ares, String landDestination, String reparationType, String houseType, String floor, String heating, String constructionCompany, int rooms, int bathrooms, int balconies, String description, long[] facilities, MultipartFile[] images) throws LocationException, AgentException, FacilityException, IOException, EstateException {
        Location location = locationRepository.findById(sectorId).orElseThrow(() -> new LocationException("Location with id " + sectorId + " doesn't exist"));
        Agent agent = agentRepository.findById(agentId).orElseThrow(() -> new AgentException("Agent with id " + agentId + " doesn't exist"));
        List<String> exceptions = new ArrayList<>();
        Estate estate = Estate.builder()
                .title(title)
                .street(street)
                .mapUrl(mapUrl)
                .type(getEstateTypeFromString(type))
                .service(getServiceTypeFromString(service))
                .price(price)
                .sector(location)
                .constructionType(getConstructionTypeFromString(constructionType))
                .agent(agent)
                .squareMeters(squareMeters)
                .ares(ares)
                .landDestination(getLandDestinationFromString(landDestination))
                .reparationType(getReparationTypeFromString(reparationType))
                .houseType(getHouseTypeFromString(houseType))
                .floor(floor)
                .heating(getHeatingTypeFromString(heating))
                .constructionCompany(constructionCompany)
                .rooms(rooms)
                .balconies(balconies)
                .bathrooms(bathrooms)
                .description(description)
                .facilities(new ArrayList<>())
                .images(new ArrayList<>())
                .build();
        estate = estateRepository.save(estate);
        long id = estate.getId();
        if (facilities != null) {
            for (long facilityId : facilities) {
                Facility facility = facilityRepository.findById(facilityId).orElseThrow(() -> new FacilityException("Facility with id " + facilityId + " doesn't exist"));
                EstateFacility estateFacility = EstateFacility.builder()
                        .estate(estate)
                        .facility(facility)
                        .build();
                estate.getFacilities().add(estateFacility);
                facility.getEstates().add(estateFacility);
            }
        }
        for (MultipartFile image : images) {
            if (image != null && image.getOriginalFilename() != null && image.getOriginalFilename().matches(".+\\.(png|jpg|jpeg|webp|jpeg2000)")) {
                String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
                String path = FileSaver.save(image, "estates", ImageKey.ORIGINAL.getKey() + id + extension);
                String originalFilename = path != null ? path.substring(path.lastIndexOf("/") + 1) : null;
                EstateImage img = estateImageRepository.save(
                        EstateImage.builder()
                                .filename(originalFilename)
                                .key(ImageKey.ORIGINAL)
                                .path(path)
                                .estate(estate)
                                .build()
                );
                estate.getImages().add(img);
                path = FileSaver.saveResized("estates", originalFilename, ImageKey.THUMBNAIL.getKey() + id + extension);
                originalFilename = path != null ? path.substring(path.lastIndexOf("/") + 1) : null;
                img = estateImageRepository.save(
                        EstateImage.builder()
                                .filename(originalFilename)
                                .key(ImageKey.THUMBNAIL)
                                .path(path)
                                .estate(estate)
                                .build()
                );
                estate.getImages().add(img);
            }
            else {
                exceptions.add("Image " + image.getOriginalFilename() + " is corrupted " +
                        "or doesn't have png, jpg, jpeg, webp or jpeg2000 extension.");
            }
        }
        estate.setCreatedAt(LocalDateTime.now());
        return new ResponsePair<>(estate.toDTO(), exceptions);
    }

    @Override
    @Transactional
    public ResponsePair<EstateDTO> update(long id, String title, String street, String mapUrl, String type, String service, double price, long sectorId, String constructionType, long agentId, double squareMeters, double ares, String landDestination, String reparationType, String houseType, String floor, String heating, String constructionCompany, int rooms, int bathrooms, int balconies, String description, long[] facilities, MultipartFile[] images, long[] deletedImages) throws EstateException, AgentException, LocationException, FacilityException, IOException {
        Estate estate = estateRepository.findById(id).orElseThrow(() -> new EstateException("Estate with id " + id + " doesn't exist"));
        Agent agent = null;
        Location location = null;
        List<String> exceptions = new ArrayList<>();
        if (agentId > 0) {
            agent = agentRepository.findById(agentId).orElseThrow(() -> new AgentException("Agent with id " + agentId + " doesn't exist"));
        }
        if (sectorId > 0) {
            location = locationRepository.findById(sectorId).orElseThrow(() -> new LocationException("Location with id " + sectorId + " doesn't exist"));
        }
        if (title != null) {
            estate.setTitle(title);
        }
        if (street != null) {
            estate.setStreet(street);
        }
        if (mapUrl != null) {
            estate.setMapUrl(mapUrl);
        }
        if (type != null) {
            estate.setType(getEstateTypeFromString(type));
        }
        if (service != null) {
            estate.setService(getServiceTypeFromString(service));
        }
        if (price > 0) {
            estate.setPrice(price);
        }
        if (location != null) {
            estate.setSector(location);
        }
        if (constructionType != null) {
            estate.setConstructionType(getConstructionTypeFromString(constructionType));
        }
        if (agent != null) {
            estate.setAgent(agent);
        }
        if (squareMeters > 0) {
            estate.setSquareMeters(squareMeters);
        }
        if (ares > 0) {
            estate.setAres(ares);
        }
        if (landDestination != null) {
            estate.setLandDestination(getLandDestinationFromString(landDestination));
        }
        if (reparationType != null) {
            estate.setReparationType(getReparationTypeFromString(reparationType));
        }
        if (houseType != null) {
            estate.setHouseType(getHouseTypeFromString(houseType));
        }
        if (floor != null) {
            estate.setFloor(floor);
        }
        if (heating != null) {
            estate.setHeating(getHeatingTypeFromString(heating));
        }
        if (constructionCompany != null) {
            estate.setConstructionCompany(constructionCompany);
        }
        if (rooms > 0) {
            estate.setRooms(rooms);
        }
        if (bathrooms > 0) {
            estate.setBathrooms(bathrooms);
        }
        if (balconies > 0) {
            estate.setBalconies(balconies);
        }
        if (description != null) {
            estate.setDescription(description);
        }
        if (facilities != null) {
            List<Long> ids = estate.getFacilities().stream()
                    .map(EstateFacility::getFacility)
                    .map(Facility::getId)
                    .collect(Collectors.toList());
            long[] facilitiesToAdd = Arrays.stream(facilities)
                    .filter(facility -> !ids.contains(facility))
                    .toArray();
            List<Long> facilitiesToDelete = ids.stream()
                    .filter(facility -> Arrays.stream(facilities).noneMatch(facilityId -> facility == facilityId))
                    .collect(Collectors.toList());

            for (long facilityId : facilitiesToAdd) {
                Facility facility = facilityRepository.findById(facilityId).orElseThrow(() -> new FacilityException("Facility with id " + facilityId + " doesn't exist"));
                EstateFacility estateFacility = EstateFacility.builder()
                        .estate(estate)
                        .facility(facility)
                        .build();
                estate.getFacilities().add(estateFacility);
                facility.getEstates().add(estateFacility);
            }

            for (long facilityId : facilitiesToDelete) {
                Facility facility = facilityRepository.findById(facilityId).orElseThrow(() -> new FacilityException("Facility with id " + facilityId + " doesn't exist"));
                EstateFacility estateFacility = estate.getFacilities().stream()
                        .filter(facility1 -> facility1.getFacility().getId() == facilityId)
                        .findFirst()
                        .orElseThrow(() -> new FacilityException("Facility with id " + facilityId + " doesn't exist"));
                estate.getFacilities().remove(estateFacility);
                facility.getEstates().remove(estateFacility);
                estateFacilityRepository.deleteById(estateFacility.getId());
            }

        }
        if (deletedImages != null) {
            for (long imageId : deletedImages) {
                EstateImage imageToDelete = estateImageRepository.findById(imageId).orElseThrow(() -> new EstateException("Image with id " + imageId + " doesn't exist"));
                String filename = imageToDelete.getFilename().substring(imageToDelete.getKey().getKey().length());
                List<EstateImage> imagesToDelete = estate.getImages().stream()
                        .filter(img -> img.getFilename().endsWith(filename))
                        .collect(Collectors.toList());
                for (EstateImage img : imagesToDelete) {
                    FileSaver.delete(img.getPath());
                    estate.getImages().remove(img);
                    estateImageRepository.deleteById(img.getId());
                }
            }
        }
        if (images != null) {
            for (MultipartFile image : images) {
                if (image != null && image.getOriginalFilename() != null && image.getOriginalFilename().matches(".+\\.(png|jpg|jpeg|webp|jpeg2000)")) {
                    String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
                    String path = FileSaver.save(image, "estates", ImageKey.ORIGINAL.getKey() + id + extension);
                    String originalFilename = path != null ? path.substring(path.lastIndexOf("/") + 1) : null;
                    EstateImage img = estateImageRepository.save(
                            EstateImage.builder()
                                    .filename(originalFilename)
                                    .key(ImageKey.ORIGINAL)
                                    .path(path)
                                    .estate(estate)
                                    .build()
                    );
                    estate.getImages().add(img);
                    path = FileSaver.saveResized("estates", originalFilename, ImageKey.THUMBNAIL.getKey() + id + extension);
                    originalFilename = path != null ? path.substring(path.lastIndexOf("/") + 1) : null;
                    img = estateImageRepository.save(
                            EstateImage.builder()
                                    .filename(originalFilename)
                                    .key(ImageKey.THUMBNAIL)
                                    .path(path)
                                    .estate(estate)
                                    .build()
                    );
                    estate.getImages().add(img);
                }
                else {
                    exceptions.add("Image " + image.getOriginalFilename() + " is corrupted " +
                            "or doesn't have png, jpg, jpeg, webp or jpeg2000 extension.");
                }
            }
        }
        return new ResponsePair<>(estate.toDTO(), exceptions);
    }

    @Override
    public void remove(long id) throws EstateException, IOException {
        Estate estate = estateRepository.findById(id).orElseThrow(() -> new EstateException("Estate with id " + id + " doesn't exist"));
        for (EstateImage image : estate.getImages()) {
            FileSaver.delete(image.getPath());
        }
        estateRepository.deleteById(id);
    }

    private EstateType getEstateTypeFromString(String type) throws EstateException {
        if (type == null)
            return null;
        for (EstateType estateType : EstateType.values()) {
            if (estateType.getKey().equals(type))
                return estateType;
        }
        throw new EstateException("Invalid string for estate type");
    }

    private ServiceType getServiceTypeFromString(String service) throws EstateException {
        if (service == null)
            return null;
        for (ServiceType serviceType : ServiceType.values()) {
            if (serviceType.getKey().equals(service))
                return serviceType;
        }
        throw new EstateException("Invalid string for service type");
    }

    private ConstructionType getConstructionTypeFromString(String type) throws EstateException {
        if (type == null)
            return null;
        for (ConstructionType constructionType : ConstructionType.values()) {
            if (constructionType.getKey().equals(type))
                return constructionType;
        }
        throw new EstateException("Invalid string for construction type");
    }

    private LandDestinationType getLandDestinationFromString(String destination) throws EstateException {
        if (destination == null)
            return null;
        for (LandDestinationType landDestination : LandDestinationType.values()) {
            if (landDestination.getKey().equals(destination))
                return landDestination;
        }
        throw new EstateException("Invalid string for land destination type");
    }

    private ReparationType getReparationTypeFromString(String type) throws EstateException {
        if (type == null)
            return null;
        for (ReparationType reparationType : ReparationType.values()) {
            if (reparationType.getKey().equals(type))
                return reparationType;
        }
        throw new EstateException("Invalid string for reparation type");
    }

    private HouseType getHouseTypeFromString(String type) throws EstateException {
        if (type == null)
            return null;
        for (HouseType houseType : HouseType.values()) {
            if (houseType.getKey().equals(type))
                return houseType;
        }
        throw new EstateException("Invalid string for house type");
    }

    private HeatingType getHeatingTypeFromString(String type) throws EstateException {
        if (type == null)
            return null;
        for (HeatingType heatingType : HeatingType.values()) {
            if (heatingType.getKey().equals(type))
                return heatingType;
        }
        throw new EstateException("Invalid string for heating type");
    }
}
