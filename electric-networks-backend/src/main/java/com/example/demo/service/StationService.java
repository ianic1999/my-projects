package com.example.demo.service;

import com.example.demo.dto.StationDTO;
import com.example.demo.dto.StationWithCustomersDTO;
import com.example.demo.model.exception.StationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public interface StationService {
    Page<StationDTO> getAll(int page, int perPage, String field, Sort.Direction order);
    StationDTO getById(long id) throws StationException;
    StationDTO add(StationDTO station);
    StationDTO update(StationDTO station) throws StationException;
    void remove(long id) throws StationException;
    Page<StationWithCustomersDTO> getAllWithCustomers(int page, int perPage, int month, String field, Sort.Direction order);
}
