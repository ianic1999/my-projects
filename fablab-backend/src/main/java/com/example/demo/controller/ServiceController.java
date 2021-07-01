package com.example.demo.controller;

import com.example.demo.dto.ServiceDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.ServiceException;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.ServiceService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/services")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceService serviceService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<ServiceDTO>> getAll(@RequestHeader("Accept-Language") String language,
                                                                @RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "15") int perPage,
                                                                @RequestParam(required = false) String order,
                                                                @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(serviceService.getAll(page, perPage, order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-"), search, language))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ServiceDTO>> getById(@PathVariable long id) throws ServiceException {
        return ResponseEntity.ok(
                new Response<>(serviceService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<ServiceDTO>> add(@RequestParam String title,
                                                    @RequestParam String summary,
                                                    @RequestParam MultipartFile image) throws IOException, UserException {
        return new ResponseEntity<>(
                new Response<>(serviceService.add(title, summary, image, userService.getCurrentUser().getId())),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{serviceId}")
    public ResponseEntity<Response<ServiceDTO>> updatePatch(@PathVariable long serviceId,
                                                       @RequestParam(required = false) String id,
                                                       @RequestParam(required = false) String title,
                                                       @RequestParam(required = false) String summary,
                                                       @RequestParam(required = false) MultipartFile image) throws IOException, ServiceException {
        return ResponseEntity.ok(
                new Response<>(serviceService.update(serviceId, title, summary, image))
        );
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<Response<ServiceDTO>> updatePut(@PathVariable long serviceId,
                                                            @RequestParam String id,
                                                            @RequestParam String title,
                                                            @RequestParam String summary,
                                                            @RequestParam(required = false) MultipartFile image) throws IOException, ServiceException {
        return ResponseEntity.ok(
                new Response<>(serviceService.update(serviceId, title, summary, image))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) {
        serviceService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
