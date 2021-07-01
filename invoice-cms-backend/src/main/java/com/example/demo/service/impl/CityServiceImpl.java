package com.example.demo.service.impl;

import com.example.demo.dto.CityDTO;
import com.example.demo.model.City;
import com.example.demo.repository.CityRepository;
import com.example.demo.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    @Override
    public Page<CityDTO> get(int page, int perPage, String search) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return cityRepository.findByNameContainsIgnoreCase(pageable, search).map(City::toDTO);
    }
}
