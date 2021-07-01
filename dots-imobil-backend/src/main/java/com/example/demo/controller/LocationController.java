package com.example.demo.controller;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.LocationException;
import com.example.demo.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin("*")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<LocationDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "15") int perPage) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(locationService.get(page, perPage))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<LocationDTO>> getById(@PathVariable long id) throws LocationException {
        return ResponseEntity.ok(
                new Response<>(locationService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<LocationDTO>> add(@RequestBody LocationDTO location) {
        return new ResponseEntity<>(
                new Response<>(locationService.add(location)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<LocationDTO>> update(@PathVariable long id,
                                                        @RequestBody LocationDTO location) throws LocationException {
        location.setId(id);
        return ResponseEntity.ok(
                new Response<>(locationService.update(location))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) {
        locationService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
