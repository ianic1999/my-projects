package com.example.demo.service.impl;

import com.example.demo.dto.BankAccountDTO;
import com.example.demo.dto.CompanyDTO;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.BankAccount;
import com.example.demo.model.City;
import com.example.demo.model.Company;
import com.example.demo.model.User;
import com.example.demo.model.enums.InvoiceStatus;
import com.example.demo.model.enums.UserRole;
import com.example.demo.model.exception.CompanyException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CompanyService;
import com.example.demo.util.aws.AwsS3Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final InvoiceRepository invoiceRepository;
    private final AwsS3Service awsService;

    @Override
    public Page<CompanyDTO> get(int page, int perPage, UserDTO currentUser) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return currentUser.getRole().equals(UserRole.USER.getKey())
                ? companyRepository.findByCreatedBy_Email(pageable, currentUser.getEmail()).map(Company::toDTO)
                    : companyRepository.findAll(pageable).map(Company::toDTO);
    }

    @Override
    public CompanyDTO getById(long id, UserDTO currentUser) throws CompanyException, UserException {
        Company companyFromDB = companyRepository.findById(id).orElseThrow(() -> new CompanyException("Company with id " + id + " doesn't exist"));
        if (!(currentUser.getEmail().equals(companyFromDB.getCreatedBy().getEmail()) || currentUser.getRole().equals(UserRole.ADMIN.getKey())))
            throw new UserException("You try to get a company that is not yours");
        return companyFromDB.toDTO();
    }

    @Override
    @Transactional
    public CompanyDTO add(String name, long cityId, String state, String street, MultipartFile image, String zipCode, UserDTO currentUser) throws UserException, CompanyException, IOException {
        User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow(() -> new UserException("User with email " + currentUser.getEmail() + " doesn't exist"));
        City city = cityRepository.findById(cityId).orElseThrow(() -> new CompanyException("City with id " + cityId + " doesn't exist"));
        Company companyToAdd = Company.builder()
                .name(name)
                .accounts(new ArrayList<>())
                .city(city)
                .country(city.getCountry())
                .state(state)
                .street(street)
                .zipCode(zipCode)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .updatedAt(LocalDateTime.now(ZoneOffset.UTC))
                .createdBy(user)
                .build();
        companyToAdd = companyRepository.save(companyToAdd);
        String imagePath = image != null ? awsService.uploadFile(image, "companies/" + companyToAdd.getId() + "/", image.getOriginalFilename()) : null;
        companyToAdd.setImage(imagePath);
        return companyToAdd.toDTO();
    }

    @Override
    @Transactional
    public CompanyDTO update(long id, String name, long cityId, String state, String street, MultipartFile image, String zipCode, UserDTO currentUser) throws CompanyException, UserException, IOException {
        Company companyFromDB = companyRepository.findById(id).orElseThrow(() -> new CompanyException("Company with id " + id + " doesn't exist"));
        if (!(currentUser.getEmail().equals(companyFromDB.getCreatedBy().getEmail()) || currentUser.getRole().equals(UserRole.ADMIN.getKey())))
            throw new UserException("You try to update a company that is not yours");
        if (name != null) {
            companyFromDB.setName(name);
        }
        if (cityId > 0) {
            City city = cityRepository.findById(cityId).orElseThrow(() -> new CompanyException("City with id " + cityId + " doesn't exist"));
            companyFromDB.setCity(city);
            companyFromDB.setCountry(city.getCountry());
        }
        if (state != null) {
            companyFromDB.setState(state);
        }
        if (street != null) {
            companyFromDB.setStreet(street);
        }
        if (zipCode != null) {
            companyFromDB.setZipCode(zipCode);
        }
        String imagePath = null;
        if (image != null) {
            if (companyFromDB.getImage() != null)
                awsService.deleteFile("companies/" + companyFromDB.getId() + "/" + companyFromDB.getImage());
            imagePath = awsService.uploadFile(image, "companies/" + companyFromDB.getId() + "/", image.getOriginalFilename());
        }
        if (imagePath != null) {
            companyFromDB.setImage(imagePath);
        }
        companyFromDB.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        invoiceRepository.findAll().stream()
                .filter(invoice -> {
                    try {
                        return mapper.readValue(invoice.getCompany(), CompanyDTO.class).getId() == companyFromDB.getId();
                    } catch (JsonProcessingException e) {
                        return false;
                    }
                })
                .filter(invoice -> invoice.getStatus().equals(InvoiceStatus.DRAFT))
                .forEach(invoice -> {
                    try {
                        invoice.setCompany(mapper.writeValueAsString(companyFromDB.toDTO()));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
        return companyFromDB.toDTO();
    }

    @Override
    public void remove(long id, UserDTO currentUser) throws UserException, CompanyException {
        Company company = companyRepository.findById(id).orElseThrow(() -> new CompanyException("Company with id " + id + " doesn't exist"));
        if (!(currentUser.getEmail().equals(company.getCreatedBy().getEmail()) || currentUser.getRole().equals(UserRole.ADMIN.getKey())))
            throw new UserException("You try to remove a company that is not yours");
        if (company.getImage() != null)
            awsService.deleteFile("companies/" + company.getId() + "/" + company.getImage());
        company.getCreatedBy().getCompanies().remove(company);
        companyRepository.deleteById(id);
    }

    @Override
    public List<BankAccountDTO> getAccountsForCompany(long companyId, UserDTO currentUser) throws CompanyException, UserException {
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new CompanyException("Company with id " + companyId + " doesn't exist"));
        if (!company.getCreatedBy().getEmail().equals(currentUser.getEmail()) && !currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new UserException("You try to get bank accounts that are not yours");
        return company.getAccounts().stream()
                .map(BankAccount::toDTO)
                .collect(Collectors.toList());
    }
}
