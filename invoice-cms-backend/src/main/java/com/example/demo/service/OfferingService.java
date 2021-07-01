package com.example.demo.service;

import com.example.demo.dto.OfferingDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.OfferingRequest;
import com.example.demo.model.exception.InvoiceException;
import com.example.demo.model.exception.OfferingException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;

public interface OfferingService {
    Page<OfferingDTO> get(int page, int perPage, UserDTO currentUser, String search);
    OfferingDTO getById(long id, UserDTO currentUser) throws OfferingException, UserException;
    OfferingDTO add(OfferingRequest offering, UserDTO currentUser) throws OfferingException, InvoiceException, UserException;
    OfferingDTO update(OfferingRequest offering, UserDTO currentUser) throws OfferingException, InvoiceException, UserException;
    void remove(long id, UserDTO currentUser) throws OfferingException, UserException;
}
