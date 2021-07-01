package com.example.demo.service.impl;

import com.example.demo.dto.FacilityDTO;
import com.example.demo.model.Facility;
import com.example.demo.model.exception.FacilityException;
import com.example.demo.repository.FacilityRepository;
import com.example.demo.service.FacilityService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepository facilityRepository;

    @Override
    public Page<FacilityDTO> get(int page, int perPage, String search) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return facilityRepository.findByNameContainsIgnoreCase(pageable, search).map(Facility::toDTO);
    }

    @Override
    public FacilityDTO getById(long id) throws FacilityException {
        return facilityRepository.findById(id).orElseThrow(() -> new FacilityException("Facility with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    public FacilityDTO add(FacilityDTO facility) {
        Facility facilityToAdd = Facility.builder()
                .name(facility.getName())
                .description(facility.getDescription())
                .icon(facility.getIcon())
                .build();
        return facilityRepository.save(facilityToAdd).toDTO();
    }

    @Override
    @Transactional
    public FacilityDTO update(FacilityDTO facility) throws FacilityException {
        Facility facilityFromDB = facilityRepository.findById(facility.getId()).orElseThrow(() -> new FacilityException("Facility with id " + facility.getId() + " doesn't exist"));
        if (facility.getName() != null) {
            facilityFromDB.setName(facility.getName());
        }
        if (facility.getIcon() != null) {
            facilityFromDB.setIcon(facility.getIcon());
        }
        if (facility.getDescription() != null) {
            facilityFromDB.setDescription(facility.getDescription());
        }
        return facilityFromDB.toDTO();
    }

    @Override
    public void remove(long id) {
        facilityRepository.deleteById(id);
    }
}
