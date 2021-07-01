package com.example.demo.controller;

import com.example.demo.dto.PartnerDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.PartnerException;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.PartnerService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/partners")
@CrossOrigin("*")
@RequiredArgsConstructor
public class PartnerController {
    private final PartnerService partnerService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<PartnerDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "15") int perPage,
                                                                @RequestParam(required = false) String order,
                                                                @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(partnerService.getAll(page, perPage, order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-"), search))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<PartnerDTO>> getById(@PathVariable long id) throws PartnerException {
        return ResponseEntity.ok(
                new Response<>(partnerService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<PartnerDTO>> add(@RequestParam String name,
                                                    @RequestParam MultipartFile image,
                                                    @RequestParam String link) throws IOException, UserException {
        return new ResponseEntity<>(
                new Response<>(partnerService.add(name, image, link, userService.getCurrentUser().getId())),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{partnerId}")
    public ResponseEntity<Response<PartnerDTO>> updatePatch(@PathVariable long partnerId,
                                                       @RequestParam(required = false) long id,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(required = false) MultipartFile image,
                                                       @RequestParam(required = false) String link) throws IOException, PartnerException {
        return ResponseEntity.ok(
                new Response<>(partnerService.update(partnerId, name, image, link))
        );
    }

    @PutMapping("/{partnerId}")
    public ResponseEntity<Response<PartnerDTO>> updatePut(@PathVariable long partnerId,
                                                            @RequestParam long id,
                                                            @RequestParam String name,
                                                            @RequestParam(required = false) MultipartFile image,
                                                            @RequestParam String link) throws IOException, PartnerException {
        return ResponseEntity.ok(
                new Response<>(partnerService.update(partnerId, name, image, link))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws IOException, PartnerException {
        partnerService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
