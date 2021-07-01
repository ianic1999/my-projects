package com.example.demo.service.impl;

import com.example.demo.dto.LocationDTO;
import com.example.demo.model.Location;
import com.example.demo.model.LocationTitle;
import com.example.demo.model.exception.LocationException;
import com.example.demo.repository.LocationRepository;
import com.example.demo.repository.LocationTitleRepository;
import com.example.demo.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationTitleRepository locationTitleRepository;

    @Override
    public Page<LocationDTO> get(int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return locationRepository.findAll(pageable).map(Location::toDTO);
    }

    @Override
    public LocationDTO getById(long id) throws LocationException {
        return locationRepository.findById(id).orElseThrow(() -> new LocationException("Location with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    @Transactional
    public LocationDTO add(LocationDTO location) {
        LocationTitle locationTitle = LocationTitle.builder()
                .ro(location.getTitle().getRo())
                .ru(location.getTitle().getRu())
                .en(location.getTitle().getEn())
                .build();
        locationTitle = locationTitleRepository.save(locationTitle);
        Location locationToAdd = Location.builder()
                .title(locationTitle)
                .build();
        locationToAdd = locationRepository.save(locationToAdd);
        locationTitle.setLocation(locationToAdd);
        return locationToAdd.toDTO();
    }

    @Override
    @Transactional
    public LocationDTO update(LocationDTO location) throws LocationException {
        Location locationFromDB = locationRepository.findById(location.getId()).orElseThrow(() -> new LocationException("Location with id " + location.getId() + " doesn't exist"));
        if (location.getTitle() != null) {
            LocationTitle title = locationFromDB.getTitle();
            if (location.getTitle().getRo() != null)
                title.setRo(location.getTitle().getRo());
            if (location.getTitle().getRu() != null)
                title.setRu(location.getTitle().getRu());
            if (location.getTitle().getEn() != null)
                title.setEn(location.getTitle().getEn());
        }
        return locationFromDB.toDTO();
    }

    @Override
    public void remove(long id) {
        locationRepository.deleteById(id);
    }
}
