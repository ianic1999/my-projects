package com.example.demo.service;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.exception.CustomerException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

public interface CustomerService {
    Page<CustomerDTO> get(int page, int perPage, UserDTO currentUser);
    CustomerDTO getById(long id, UserDTO currentUser) throws CustomerException, UserException;
    CustomerDTO add(String firstName,
                    String lastName,
                    String email,
                    MultipartFile image,
                    String companyName,
                    long cityId,
                    String street,
                    String state,
                    String zipCode,
                    UserDTO currentUser) throws CustomerException, IOException, UserException;
    CustomerDTO update(long id,
                        String firstName,
                        String lastName,
                        String email,
                        MultipartFile image,
                        String companyName,
                        long cityId,
                        String street,
                        String state,
                        String zipCode,
                        UserDTO currentUser) throws CustomerException, IOException, UserException;
    void remove(long id, UserDTO currentUser) throws CustomerException, IOException, UserException;
}
