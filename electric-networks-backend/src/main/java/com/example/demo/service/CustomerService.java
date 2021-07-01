package com.example.demo.service;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.request.CustomerRequest;
import com.example.demo.model.exception.CustomerException;
import com.example.demo.model.exception.StationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface CustomerService {
    Page<CustomerDTO> getAll(int page, int perPage, String field, Sort.Direction order, String fullName, String contractNumber, String meter, long stationId, String address, String isCompleted);
    CustomerDTO getById(long id) throws CustomerException;
    CustomerDTO add(CustomerRequest customer) throws StationException;
    CustomerDTO update(CustomerRequest customer) throws StationException, CustomerException;
    void remove(long id);
    int addFromXLS(long stationId, MultipartFile file) throws IOException, StationException, CustomerException;
}
