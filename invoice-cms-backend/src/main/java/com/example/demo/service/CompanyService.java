package com.example.demo.service;

import com.example.demo.dto.BankAccountDTO;
import com.example.demo.dto.CompanyDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.exception.CompanyException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CompanyService {
    Page<CompanyDTO> get(int page, int perPage, UserDTO currentUser);
    CompanyDTO getById(long id, UserDTO currentUser) throws CompanyException, UserException;
    CompanyDTO add(String name,
                   long cityId,
                   String state,
                   String street,
                   MultipartFile image,
                   String zipCode,
                   UserDTO currentUser) throws UserException, CompanyException, IOException;
    CompanyDTO update(long id,
                      String name,
                      long cityId,
                      String state,
                      String street,
                      MultipartFile image,
                      String zipCode,
                      UserDTO currentUser) throws CompanyException, UserException, IOException;
    void remove(long id, UserDTO currentUser) throws UserException, CompanyException;
    List<BankAccountDTO> getAccountsForCompany(long companyId, UserDTO currentUser) throws CompanyException, UserException;
}
