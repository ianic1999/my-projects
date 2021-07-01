package com.example.demo.service;

import com.example.demo.dto.BankAccountDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.BankAccountRequest;
import com.example.demo.model.exception.BankAccountException;
import com.example.demo.model.exception.CompanyException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;

public interface BankAccountService {
    Page<BankAccountDTO> get(int page, int perPage, UserDTO currentUser);
    BankAccountDTO getById(long id, UserDTO currentUser) throws BankAccountException, UserException;
    BankAccountDTO add(BankAccountRequest request, UserDTO currentUser) throws BankAccountException, CompanyException, UserException;
    BankAccountDTO update(BankAccountRequest request,  UserDTO currentUser) throws BankAccountException, UserException, CompanyException;
    void remove(long id, UserDTO currentUser) throws UserException, BankAccountException;
}
