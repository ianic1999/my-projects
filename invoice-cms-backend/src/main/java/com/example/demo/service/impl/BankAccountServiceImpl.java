package com.example.demo.service.impl;

import com.example.demo.dto.BankAccountDTO;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.BankAccountRequest;
import com.example.demo.model.*;
import com.example.demo.model.enums.InvoiceStatus;
import com.example.demo.model.enums.UserRole;
import com.example.demo.model.exception.BankAccountException;
import com.example.demo.model.exception.CompanyException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.*;
import com.example.demo.service.BankAccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final CompanyRepository companyRepository;
    private final CityRepository cityRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Override
    public Page<BankAccountDTO> get(int page, int perPage, UserDTO currentUser) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return currentUser.getRole().equals(UserRole.USER.getKey())
                ? bankAccountRepository.findByCreatedBy_Email(pageable, currentUser.getEmail()).map(BankAccount::toDTO)
                    : bankAccountRepository.findAll(pageable).map(BankAccount::toDTO);
    }

    @Override
    public BankAccountDTO getById(long id, UserDTO currentUser) throws BankAccountException, UserException {
        BankAccount account = bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountException("Bank account with id " + id + " doesn't exist"));
        if (!account.getCreatedBy().getEmail().equals(currentUser.getEmail()) && !currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new UserException("You try to get a bank account that is not yours");
        return account.toDTO();
    }

    @Override
    public BankAccountDTO add(BankAccountRequest request, UserDTO currentUser) throws BankAccountException, CompanyException, UserException {
        City city = cityRepository.findById(request.getCityId()).orElseThrow(() -> new BankAccountException("City with id " + request.getCityId() + " doesn't exist"));
        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> new CompanyException("Company with id " + request.getCompanyId() + " doesn't exist"));
        User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow(() -> new UserException("User with email " + currentUser.getEmail() + " doesn't exist"));
        BankAccount account = BankAccount.builder()
                .name(request.getName())
                .iban(request.getIban())
                .swiftBic(request.getSwiftBic())
                .street(request.getStreet())
                .postcode(request.getPostcode())
                .city(city)
                .country(city.getCountry())
                .createdBy(user)
                .company(company)
                .build();
        return bankAccountRepository.save(account).toDTO();
    }

    @Override
    @Transactional
    public BankAccountDTO update(BankAccountRequest request, UserDTO currentUser) throws BankAccountException, UserException, CompanyException {
        BankAccount account = bankAccountRepository.findById(request.getId()).orElseThrow(() -> new BankAccountException("Bank account with id " + request.getId() + " doesn't exist"));
        if (!account.getCreatedBy().getEmail().equals(currentUser.getEmail()) && !currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new UserException("You try to update a bank account that is not yours");
        if (request.getName() != null) {
            account.setName(request.getName());
        }
        if (request.getIban() != null) {
            account.setIban(request.getIban());
        }
        if (request.getSwiftBic() != null) {
            account.setSwiftBic(request.getSwiftBic());
        }
        if (request.getStreet() != null) {
            account.setStreet(request.getStreet());
        }
        if (request.getCityId() > 0) {
            City city = cityRepository.findById(request.getCityId()).orElseThrow(() -> new BankAccountException("City with id " + request.getCityId() + " doesn't exist"));
            account.setCity(city);
            account.setCountry(city.getCountry());
        }
        if (request.getCompanyId() > 0) {
            Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> new CompanyException("Company with id " + request.getCompanyId() + " doesn't exist"));
            account.setCompany(company);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        invoiceRepository.findAll().stream()
                .filter(invoice -> {
                    try {
                        return mapper.readValue(invoice.getBankAccount(), BankAccountDTO.class).getId() == account.getId();
                    } catch (JsonProcessingException e) {
                        return false;
                    }
                })
                .filter(invoice -> invoice.getStatus().equals(InvoiceStatus.DRAFT))
                .forEach(invoice -> {
                    try {
                        invoice.setBankAccount(mapper.writeValueAsString(account.toDTO()));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
        return account.toDTO();
    }

    @Override
    public void remove(long id, UserDTO currentUser) throws UserException, BankAccountException {
        BankAccount account = bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountException("Bank account with id " + id + " doesn't exist"));
        if (!account.getCreatedBy().getEmail().equals(currentUser.getEmail()) && !currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new UserException("You try to remove a bank account that is not yours");
        try {
            bankAccountRepository.deleteById(id);
        } catch (Exception e) {
            throw new BankAccountException("Some invoices use this account. Please remove them firstly");
        }
    }
}
