package com.example.demo.service;

import com.example.demo.dto.IndicationDTO;
import com.example.demo.dto.IndicationsPerMonthDTO;
import com.example.demo.dto.IndicationsPerMonthResponseDTO;
import com.example.demo.dto.request.IndicationRequest;
import com.example.demo.model.exception.CustomerException;
import com.example.demo.model.exception.IndicationException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IndicationService {
    Page<IndicationDTO> getAll(int page, int perPage, long customerId, int year);
    IndicationDTO getById(long id) throws IndicationException;
    IndicationDTO add(IndicationRequest indication) throws CustomerException, IndicationException;
    IndicationDTO update(IndicationRequest indication) throws CustomerException, IndicationException;
    void remove(long id);
    IndicationDTO getLast(long customerId) throws IndicationException;
    IndicationsPerMonthResponseDTO getIndicationsPerMonth();
}
