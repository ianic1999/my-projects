package com.example.demo.controller;

import com.example.demo.dto.BankAccountDTO;
import com.example.demo.dto.CompanyDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.CompanyException;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.CompanyService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<CompanyDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "15") int perPage) throws UserException {
        return ResponseEntity.ok(
                new PaginatedResponse<>(companyService.get(page, perPage, userService.getCurrentUser()))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<CompanyDTO>> getById(@PathVariable long id) throws CompanyException, UserException {
        return ResponseEntity.ok(
                new Response<>(companyService.getById(id, userService.getCurrentUser()))
        );
    }

    @GetMapping("/bank_accounts/{id}")
    public ResponseEntity<Response<List<BankAccountDTO>>> getAccountsForCompany(@PathVariable long id) throws UserException, CompanyException {
        return ResponseEntity.ok(
                new Response<>(companyService.getAccountsForCompany(id, userService.getCurrentUser()))
        );
    }

    @PostMapping
    public ResponseEntity<Response<CompanyDTO>> add(@RequestParam String name,
                                                    @RequestParam(defaultValue = "0") long cityId,
                                                    @RequestParam String street,
                                                    @RequestParam(required = false) String state,
                                                    @RequestParam(required = false) MultipartFile image,
                                                    @RequestParam(required = false) String zipCode) throws UserException, CompanyException, IOException {
        return new ResponseEntity<>(
                new Response<>(companyService.add(name, cityId, state, street, image, zipCode, userService.getCurrentUser())),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<CompanyDTO>> update(@PathVariable long id,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(defaultValue = "0") long cityId,
                                                       @RequestParam(required = false) String street,
                                                       @RequestParam(required = false) String state,
                                                       @RequestParam(required = false) MultipartFile image,
                                                       @RequestParam(required = false) String zipCode) throws CompanyException, UserException, IOException {
        return ResponseEntity.ok(
                new Response<>(companyService.update(id, name, cityId, state, street, image, zipCode, userService.getCurrentUser()))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws UserException, CompanyException {
        companyService.remove(id, userService.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}
