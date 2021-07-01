package com.example.demo.service.impl;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.*;
import com.example.demo.model.enums.InvoiceStatus;
import com.example.demo.model.enums.UserRole;
import com.example.demo.model.exception.CustomerException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CustomerService;
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

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CityRepository cityRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final AwsS3Service awsService;

    @Override
    public Page<CustomerDTO> get(int page, int perPage, UserDTO currentUser) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return currentUser.getRole().equals(UserRole.USER.getKey())
            ? customerRepository.findByCreatedBy_Email(pageable, currentUser.getEmail()).map(Customer::toDTO)
                : customerRepository.findAll(pageable).map(Customer::toDTO);
    }

    @Override
    public CustomerDTO getById(long id, UserDTO currentUser) throws CustomerException, UserException {
        Customer customerFromDB = customerRepository.findById(id).orElseThrow(() -> new CustomerException("Customer with id " + id + " doesn't exist"));
        if (!(customerFromDB.getCreatedBy().getEmail().equals(currentUser.getEmail()) || currentUser.getRole().equals(UserRole.ADMIN.getKey())))
            throw new UserException("You try to get a customer that is not yours");
        return customerFromDB.toDTO();
    }

    @Override
    @Transactional
    public CustomerDTO add(String firstName, String lastName, String email, MultipartFile image, String companyName, long cityId, String street, String state, String zipCode, UserDTO currentUser) throws CustomerException, IOException, UserException {
        City city = cityRepository.findById(cityId).orElseThrow(() -> new CustomerException("City with id " + cityId + " doesn't exist"));
        User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow(() -> new UserException("User with email " + currentUser.getEmail() + " doesn't exist"));
        Customer customer = Customer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .companyName(companyName)
                .country(city.getCountry())
                .city(city)
                .street(street)
                .state(state)
                .zipCode(zipCode)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .updatedAt(LocalDateTime.now(ZoneOffset.UTC))
                .createdBy(user)
                .build();
        customer = customerRepository.save(customer);
        String imagePath = image != null ? awsService.uploadFile(image, "customers/" + customer.getId() + "/", image.getOriginalFilename()) : null;
        customer.setImage(imagePath);
        return customer.toDTO();
    }

    @Override
    @Transactional
    public CustomerDTO update(long id, String firstName, String lastName, String email, MultipartFile image, String companyName, long cityId, String street, String state, String zipCode, UserDTO currentUser) throws CustomerException, IOException, UserException {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerException("Customer with id " + id + " doesn't exist"));
        if (!(customer.getCreatedBy().getEmail().equals(currentUser.getEmail()) || currentUser.getRole().equals(UserRole.ADMIN.getKey())))
            throw new UserException("You try to update a customer that is not yours");
        City city = cityId > 0 ? cityRepository.findById(cityId).orElseThrow(() -> new CustomerException("City with id " + cityId + " doesn't exist")) : null;
        String imagePath = null;
        if (image != null) {
            if (customer.getImage() != null)
                awsService.deleteFile("customers/" + customer.getId() + "/" + customer.getImage());
            imagePath = awsService.uploadFile(image, "customers/" + customer.getId() + "/", image.getOriginalFilename());
        }
        if (firstName != null) {
            customer.setFirstName(firstName);
        }
        if (lastName != null) {
            customer.setLastName(lastName);
        }
        if (email != null) {
            customer.setEmail(email);
        }
        if (companyName != null) {
            customer.setCompanyName(companyName);
        }
        if (imagePath != null) {
            customer.setImage(imagePath);
        }
        if (city != null) {
            customer.setCity(city);
            customer.setCountry(city.getCountry());
        }
        if (street != null) {
            customer.setStreet(street);
        }
        if (state != null) {
            customer.setState(state);
        }
        if (zipCode != null) {
            customer.setZipCode(zipCode);
        }
        customer.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        invoiceRepository.findAll().stream()
                .filter(invoice -> {
                    try {
                        return mapper.readValue(invoice.getCustomer(), CustomerDTO.class).getId() == customer.getId();
                    } catch (JsonProcessingException e) {
                        return false;
                    }
                })
                .filter(invoice -> invoice.getStatus().equals(InvoiceStatus.DRAFT))
                .forEach(invoice -> {
                    try {
                        invoice.setCustomer(mapper.writeValueAsString(customer.toDTO()));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
        return customer.toDTO();
    }

    @Override
    public void remove(long id, UserDTO currentUser) throws CustomerException, UserException {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerException("Customer with id " + id + " doesn't exist"));
        if (!(customer.getCreatedBy().getEmail().equals(currentUser.getEmail()) || currentUser.getRole().equals(UserRole.ADMIN.getKey())))
            throw new UserException("You try to remove a customer that is not yours");
        if (customer.getImage() != null)
            awsService.deleteFile("customers/" + customer.getId() + "/" + customer.getImage());
        customerRepository.deleteById(id);
    }
}
