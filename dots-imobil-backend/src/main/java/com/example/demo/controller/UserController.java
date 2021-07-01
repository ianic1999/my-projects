package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.JwtRequest;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<UserDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "15") int perPage) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(userService.get(page, perPage))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<UserDTO>> getById(@PathVariable long id) throws UserException {
        return ResponseEntity.ok(
                new Response<>(userService.getById(id))
        );
    }

    @GetMapping("/me")
    public ResponseEntity<Response<UserDTO>> getCurrentUser() throws UserException {
        return ResponseEntity.ok(
                new Response<>(userService.getCurrentUser())
        );
    }

    @PostMapping
    public ResponseEntity<Response<UserDTO>> add(@RequestParam String email,
                                                     @RequestParam String fullName,
                                                     @RequestParam MultipartFile image,
                                                     @RequestParam(required = false) String password) throws IOException, UserException {
        password = passwordEncoder.encode(password);
        return new ResponseEntity<>(
                new Response<>(userService.add(email, fullName, password, image)),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Response<UserDTO>> updatePatch(@PathVariable long userId,
                                                         @RequestParam long id,
                                                         @RequestParam(required = false) String email,
                                                         @RequestParam(required = false) String fullName,
                                                         @RequestParam(required = false) MultipartFile image,
                                                         @RequestParam(required = false) String password) throws IOException, UserException {
        return ResponseEntity.ok(
                new Response<>(userService.update(userId, email, fullName, password, image))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws IOException, UserException {
        userService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
