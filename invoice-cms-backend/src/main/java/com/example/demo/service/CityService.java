package com.example.demo.service;

import com.example.demo.dto.CityDTO;
import org.springframework.data.domain.Page;

public interface CityService {
    Page<CityDTO> get(int page, int perPage, String search);
}
