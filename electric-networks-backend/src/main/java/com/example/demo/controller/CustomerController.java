package com.example.demo.controller;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.request.CustomerRequest;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.CustomerException;
import com.example.demo.model.exception.StationException;
import com.example.demo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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

    @GetMapping
    public ResponseEntity<PaginatedResponse<CustomerDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "15") int perPage,
                                                                 @RequestParam(required = false) String order,
                                                                 @RequestParam(required = false) String fullName,
                                                                 @RequestParam(required = false) String contractNumber,
                                                                 @RequestParam(required = false) String meter,
                                                                 @RequestParam(defaultValue = "0") long station,
                                                                 @RequestParam(required = false) String address,
                                                                 @RequestParam(required = false) String isCompleted) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(customerService.getAll(page, perPage, order == null ? null : order.startsWith("-") ? order.substring(1) : order, order == null ? null : order.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC, fullName, contractNumber, meter, station, address, isCompleted))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<CustomerDTO>> getById(@PathVariable long id) throws CustomerException {
        return ResponseEntity.ok(
                new Response<>(customerService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<CustomerDTO>> add(@RequestBody CustomerRequest customer) throws StationException {
        return new ResponseEntity<>(
                new Response<>(customerService.add(customer)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<CustomerDTO>> update(@PathVariable long id,
                                                        @RequestBody CustomerRequest customer) throws StationException, CustomerException {
        customer.setId(id);
        return ResponseEntity.ok(
                new Response<>(customerService.update(customer))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) {
        customerService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/from_xls")
    public ResponseEntity<Response<Integer>> addFromXLS(@RequestParam long stationId,
                                                        @RequestParam MultipartFile file) throws IOException, StationException, CustomerException {
        return ResponseEntity.ok(
                new Response<>(customerService.addFromXLS(stationId, file))
        );
    }
}
