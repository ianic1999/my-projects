package com.example.demo.controller;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.CustomerException;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.CustomerService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<CustomerDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "15") int perPage) throws UserException {
        return ResponseEntity.ok(
                new PaginatedResponse<>(customerService.get(page, perPage, userService.getCurrentUser()))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<CustomerDTO>> getById(@PathVariable long id) throws CustomerException, UserException {
        return ResponseEntity.ok(
                new Response<>(customerService.getById(id, userService.getCurrentUser()))
        );
    }

    @PostMapping
    public ResponseEntity<Response<CustomerDTO>> add(@RequestParam String firstName,
                                                     @RequestParam String lastName,
                                                     @RequestParam String email,
                                                     @RequestParam(required = false) MultipartFile image,
                                                     @RequestParam(required = false) String companyName,
                                                     @RequestParam(defaultValue = "0") long cityId,
                                                     @RequestParam String street,
                                                     @RequestParam(required = false) String state,
                                                     @RequestParam(required = false) String zipCode) throws CustomerException, IOException, UserException {
        return new ResponseEntity<>(
                new Response<>(customerService.add(firstName, lastName, email, image, companyName, cityId, street, state, zipCode, userService.getCurrentUser())),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Response<CustomerDTO>> updatePut(@PathVariable long customerId,
                                                           @RequestParam long id,
                                                           @RequestParam String firstName,
                                                           @RequestParam String lastName,
                                                           @RequestParam String email,
                                                           @RequestParam(required = false) MultipartFile image,
                                                           @RequestParam(required = false) String companyName,
                                                           @RequestParam(defaultValue = "0") long cityId,
                                                           @RequestParam String street,
                                                           @RequestParam(required = false) String state,
                                                           @RequestParam(required = false) String zipCode) throws CustomerException, IOException, UserException {
        return ResponseEntity.ok(
                new Response<>(customerService.update(customerId, firstName, lastName, email, image, companyName, cityId, street, state, zipCode, userService.getCurrentUser()))
        );
    }

    @PatchMapping("/{customerId}")
    public ResponseEntity<Response<CustomerDTO>> updatePatch(@PathVariable long customerId,
                                                           @RequestParam long id,
                                                           @RequestParam(required = false) String firstName,
                                                           @RequestParam(required = false) String lastName,
                                                           @RequestParam(required = false) String email,
                                                           @RequestParam(required = false) MultipartFile image,
                                                           @RequestParam(required = false) String companyName,
                                                           @RequestParam(defaultValue = "-1") long cityId,
                                                           @RequestParam(required = false) String street,
                                                           @RequestParam(required = false) String state,
                                                           @RequestParam(required = false) String zipCode) throws CustomerException, IOException, UserException {
        return ResponseEntity.ok(
                new Response<>(customerService.update(customerId, firstName, lastName, email, image, companyName, cityId, street, state, zipCode, userService.getCurrentUser()))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws IOException, CustomerException, UserException {
        customerService.remove(id, userService.getCurrentUser());
        return ResponseEntity.noContent().build();
    }

}
