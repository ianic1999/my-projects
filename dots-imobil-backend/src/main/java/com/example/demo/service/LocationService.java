package com.example.demo.service;

import com.example.demo.dto.LocationDTO;
import com.example.demo.model.exception.LocationException;
import org.springframework.data.domain.Page;

public interface LocationService {
    Page<LocationDTO> get(int page, int perPage);
    LocationDTO getById(long id) throws LocationException;
    LocationDTO add(LocationDTO location);
    LocationDTO update(LocationDTO location) throws LocationException;
    void remove(long id);
}
