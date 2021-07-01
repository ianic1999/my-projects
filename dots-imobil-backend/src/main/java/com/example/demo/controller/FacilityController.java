package com.example.demo.controller;

import com.example.demo.dto.FacilityDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.FacilityException;
import com.example.demo.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facilities")
@CrossOrigin("*")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<FacilityDTO>> get(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "15") int perPage,
                                                              @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(facilityService.get(page, perPage, search))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<FacilityDTO>> getById(@PathVariable long id) throws FacilityException {
        return ResponseEntity.ok(
                new Response<>(facilityService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<FacilityDTO>> add(@RequestBody FacilityDTO facility) {
        return new ResponseEntity<>(
                new Response<>(facilityService.add(facility)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<FacilityDTO>> update(@PathVariable long id,
                                                        @RequestBody FacilityDTO facility) throws FacilityException {
        facility.setId(id);
        return ResponseEntity.ok(
                new Response<>(facilityService.update(facility))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) {
        facilityService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
