package com.example.demo.controller;

import com.example.demo.dto.TestimonialDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.TestimonialException;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.TestimonialService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/testimonials")
@CrossOrigin("*")
@RequiredArgsConstructor
public class TestimonialController {
    private final TestimonialService testimonialService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<TestimonialDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "15") int perPage,
                                                                    @RequestParam(required = false) String order,
                                                                    @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(testimonialService.getAll(page, perPage, order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-"), search))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<TestimonialDTO>> getById(@PathVariable long id) throws TestimonialException {
        return ResponseEntity.ok(
                new Response<>(testimonialService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<TestimonialDTO>> add(@RequestParam String name,
                                                        @RequestParam String jobTitle,
                                                        @RequestParam MultipartFile image,
                                                        @RequestParam String message) throws IOException, UserException {
        return new ResponseEntity<>(
                new Response<>(testimonialService.add(name, jobTitle, image, message, userService.getCurrentUser().getId())),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{testimonialId}")
    public ResponseEntity<Response<TestimonialDTO>> updatePatch(@PathVariable long testimonialId,
                                                           @RequestParam(required = false) String id,
                                                           @RequestParam(required = false) String name,
                                                           @RequestParam(required = false) String jobTitle,
                                                           @RequestParam(required = false) MultipartFile image,
                                                           @RequestParam(required = false) String message) throws TestimonialException, IOException {
        return ResponseEntity.ok(
                new Response<>(testimonialService.update(testimonialId, name, jobTitle, image, message))
        );
    }

    @PutMapping("/{testimonialId}")
    public ResponseEntity<Response<TestimonialDTO>> updatePut(@PathVariable long testimonialId,
                                                                @RequestParam String id,
                                                                @RequestParam String name,
                                                                @RequestParam String jobTitle,
                                                                @RequestParam(required = false) MultipartFile image,
                                                                @RequestParam String message) throws TestimonialException, IOException {
        return ResponseEntity.ok(
                new Response<>(testimonialService.update(testimonialId, name, jobTitle, image, message))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws IOException, TestimonialException {
        testimonialService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
