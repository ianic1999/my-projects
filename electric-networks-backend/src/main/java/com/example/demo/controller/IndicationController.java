package com.example.demo.controller;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.IndicationDTO;
import com.example.demo.dto.IndicationsPerMonthDTO;
import com.example.demo.dto.IndicationsPerMonthResponseDTO;
import com.example.demo.dto.request.CustomerRequest;
import com.example.demo.dto.request.IndicationRequest;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.CustomerException;
import com.example.demo.model.exception.IndicationException;
import com.example.demo.model.exception.StationException;
import com.example.demo.service.IndicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/indications")
@CrossOrigin("*")
@RequiredArgsConstructor
public class IndicationController {
    private final IndicationService indicationService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<IndicationDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "15") int perPage,
                                                                   @RequestParam(defaultValue = "0") long customerId,
                                                                   @RequestParam(defaultValue = "0") int year) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(indicationService.getAll(page, perPage, customerId, year))
        );
    }

    @GetMapping("/last")
    public ResponseEntity<Response<IndicationDTO>> getLastByCustomerId(@RequestParam long customerId) throws IndicationException {
        return ResponseEntity.ok(
                new Response<>(indicationService.getLast(customerId))
        );
    }

    @GetMapping("/total")
    public ResponseEntity<Response<IndicationsPerMonthResponseDTO>> getAllWithAmountPerMonth() {
        return ResponseEntity.ok(
                new Response<>(indicationService.getIndicationsPerMonth())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<IndicationDTO>> getById(@PathVariable long id) throws IndicationException {
        return ResponseEntity.ok(
                new Response<>(indicationService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<IndicationDTO>> add(@RequestBody IndicationRequest indication) throws IndicationException, CustomerException {
        return new ResponseEntity<>(
                new Response<>(indicationService.add(indication)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<IndicationDTO>> update(@PathVariable long id,
                                                          @RequestBody IndicationRequest indication) throws CustomerException, IndicationException {
        indication.setId(id);
        return ResponseEntity.ok(
                new Response<>(indicationService.update(indication))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) {
        indicationService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
