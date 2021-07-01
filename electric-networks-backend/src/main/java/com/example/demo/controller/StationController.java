package com.example.demo.controller;

import com.example.demo.dto.StationDTO;
import com.example.demo.dto.StationWithCustomersDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.StationException;
import com.example.demo.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stations")
@CrossOrigin("*")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<StationDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "15") int perPage,
                                                                @RequestParam(required = false) String order) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(stationService.getAll(page, perPage, order == null ? null : order.startsWith("-") ? order.substring(1) : order, order == null ? null : order.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<StationDTO>> getById(@PathVariable long id) throws StationException {
        return ResponseEntity.ok(
                new Response<>(stationService.getById(id))
        );
    }

    @GetMapping("/customers_number")
    public ResponseEntity<PaginatedResponse<StationWithCustomersDTO>> getAllWithCustomers(@RequestParam(defaultValue = "1") int page,
                                                                                          @RequestParam(defaultValue = "15") int perPage,
                                                                                          @RequestParam(defaultValue = "0") int month,
                                                                                          @RequestParam(required = false) String order) {
        if (month == 0)
            month = LocalDate.now().getMonth().getValue();
        return ResponseEntity.ok(
                new PaginatedResponse<>(stationService.getAllWithCustomers(page, perPage, month, order == null ? null : order.startsWith("-") ? order.substring(1) : order, order == null ? null : order.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC))
        );
    }

    @PostMapping
    public ResponseEntity<Response<StationDTO>> add(@RequestBody StationDTO station) {
        return new ResponseEntity<>(
                new Response<>(stationService.add(station)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<StationDTO>> update(@PathVariable long id,
                                                       @RequestBody StationDTO station) throws StationException {
        station.setId(id);
        return ResponseEntity.ok(
                new Response<>(stationService.update(station))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws StationException {
        stationService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
