package com.example.demo.controller;

import com.example.demo.dto.CityDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<CityDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "15") int perPage,
                                                             @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(cityService.get(page, perPage, search))
        );
    }
}
