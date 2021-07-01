package com.example.demo.service;

import com.example.demo.dto.FacilityDTO;
import com.example.demo.model.exception.FacilityException;
import org.springframework.data.domain.Page;

public interface FacilityService {
    Page<FacilityDTO> get(int page, int perPage, String search);
    FacilityDTO getById(long id) throws FacilityException;
    FacilityDTO add(FacilityDTO facility);
    FacilityDTO update(FacilityDTO facility) throws FacilityException;
    void remove(long id);
}
