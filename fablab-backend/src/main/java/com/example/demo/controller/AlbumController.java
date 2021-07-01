package com.example.demo.controller;

import com.example.demo.dto.AlbumDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.AlbumException;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.AlbumService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/albums")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<AlbumDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "15") int perPage,
                                                              @RequestParam(required = false) String order,
                                                              @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(albumService.getAll(page, perPage, order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-"), search))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<AlbumDTO>> getById(@PathVariable long id) throws AlbumException {
        return ResponseEntity.ok(
                new Response<>(albumService.getById(id))
        );
    }

    @GetMapping("/active")
    public ResponseEntity<PaginatedResponse<AlbumDTO>> getAllActive(@RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "15") int perPage,
                                                                    @RequestParam(required = false) String order,
                                                                    @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(albumService.getAllActive(page, perPage, order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-"), search))
        );
    }

    @PostMapping
    public ResponseEntity<Response<AlbumDTO>> add(@RequestParam MultipartFile coverImage,
                                                  @RequestParam MultipartFile[] images,
                                                  @RequestParam(defaultValue = "false") String isPublic) throws IOException, UserException {
        return new ResponseEntity<>(
                new Response<>(albumService.add(coverImage, images, isPublic, userService.getCurrentUser().getId())),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws IOException, AlbumException {
        albumService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{albumId}")
    public ResponseEntity<Response<AlbumDTO>> update(@PathVariable long albumId,
                                                     @RequestParam(required = false) String id,
                                                     @RequestParam(required = false) MultipartFile[] images,
                                                     @RequestParam(required = false) String[] deletedImages,
                                                     @RequestParam(required = false) MultipartFile coverImage,
                                                     @RequestParam(defaultValue = "false") String isPublic) throws IOException, AlbumException {
        return ResponseEntity.ok(
                new Response<>(albumService.update(albumId, coverImage, images, deletedImages != null ? Arrays.stream(deletedImages).map(Long::parseLong).collect(Collectors.toList()) : null, isPublic))
        );
    }
}
