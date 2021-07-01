package com.example.demo.controller;

import com.example.demo.dto.SettingDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.SettingException;
import com.example.demo.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/settings")
public class SettingController {
    private final SettingService settingService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<SettingDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "15") int perPage,
                                                                @RequestParam(required = false) String order,
                                                                @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(settingService.getAll(page, perPage, order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-"), search))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<SettingDTO>> getById(@PathVariable long id) throws SettingException {
        return ResponseEntity.ok(
                new Response<>(settingService.getById(id))
        );
    }

    @GetMapping("/active")
    public ResponseEntity<PaginatedResponse<SettingDTO>> getAllActive(@RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "15") int perPage,
                                                                      @RequestParam(required = false) String order,
                                                                      @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(settingService.getAllActive(page, perPage, order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-"), search))
        );
    }

    @PostMapping
    public ResponseEntity<Response<SettingDTO>> add(@RequestBody SettingDTO dto) throws SettingException {
        return new ResponseEntity<>(
                new Response<>(settingService.add(dto)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{settingId}")
    public ResponseEntity<Response<SettingDTO>> update(@PathVariable long settingId,
                                                       @RequestBody SettingDTO dto) throws SettingException {
        return ResponseEntity.ok(
                new Response<>(settingService.update(dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) {
        settingService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
