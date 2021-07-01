package com.example.demo.controller;

import com.example.demo.dto.SpaceDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.SpaceException;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.SpaceService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/spaces")
@CrossOrigin("*")
@RequiredArgsConstructor
public class SpaceController {
    private final SpaceService spaceService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<SpaceDTO>> getAll(@RequestHeader("Accept-Language") String language,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "15") int perPage,
                                                              @RequestParam(required = false) String order,
                                                              @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(spaceService.getAll(page, perPage, order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-"), search, language))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<SpaceDTO>> getById(@PathVariable long id) throws SpaceException {
        return ResponseEntity.ok(
                new Response<>(spaceService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<SpaceDTO>> add(@RequestParam String title,
                                                  @RequestParam MultipartFile[] images,
                                                  @RequestParam double area,
                                                  @RequestParam(defaultValue = "0") double price,
                                                  @RequestParam String description) throws IOException, UserException {
        return new ResponseEntity<>(
                new Response<>(spaceService.add(title, area, price, images, description, userService.getCurrentUser().getId())),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{spaceId}")
    public ResponseEntity<Response<SpaceDTO>> updatePatch(@PathVariable long spaceId,
                                                     @RequestParam(required = false) String id,
                                                     @RequestParam(required = false) String title,
                                                     @RequestParam(required = false) String area,
                                                     @RequestParam(required = false) String price,
                                                     @RequestParam(required = false) MultipartFile[] images,
                                                     @RequestParam(required = false) String[] deletedImages,
                                                     @RequestParam(required = false) String description) throws IOException, SpaceException {
        return ResponseEntity.ok(
                new Response<>(spaceService.update(spaceId, title, area, price, images, deletedImages != null ? Arrays.stream(deletedImages).map(Long::parseLong).collect(Collectors.toList()) : null, description))
        );
    }

    @PutMapping("/{spaceId}")
    public ResponseEntity<Response<SpaceDTO>> updatePut(@PathVariable long spaceId,
                                                        @RequestParam(required = false) String id,
                                                        @RequestParam(required = false) String title,
                                                        @RequestParam(required = false) String area,
                                                        @RequestParam(required = false) String price,
                                                        @RequestParam(required = false) MultipartFile[] images,
                                                        @RequestParam(required = false) String[] deletedImages,
                                                        @RequestParam(required = false) String description) throws IOException, SpaceException {
        return ResponseEntity.ok(
                new Response<>(spaceService.update(spaceId, title, area, price, images, deletedImages != null ? Arrays.stream(deletedImages).map(Long::parseLong).collect(Collectors.toList()) : null, description))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws IOException, SpaceException {
        spaceService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
