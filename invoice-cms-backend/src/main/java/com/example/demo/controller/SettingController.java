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

@RestController
@RequestMapping("/api/settings")
@CrossOrigin("*")
@RequiredArgsConstructor
public class SettingController {
    private final SettingService settingService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<SettingDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "15") int perPage) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(settingService.get(page, perPage))
        );
    }

    @GetMapping("/active")
    public ResponseEntity<PaginatedResponse<SettingDTO>> getAllActive(@RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "15") int perPage) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(settingService.getActive(page, perPage))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<SettingDTO>> getById(@PathVariable long id) throws SettingException {
        return ResponseEntity.ok(
                new Response<>(settingService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<SettingDTO>> add(@RequestBody SettingDTO setting) throws SettingException {
        return new ResponseEntity<>(
                new Response<>(settingService.add(setting)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<SettingDTO>> update(@PathVariable long id,
                                                       @RequestBody SettingDTO setting) throws SettingException {
        setting.setId(id);
        return ResponseEntity.ok(
                new Response<>(settingService.update(setting))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) {
        settingService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
