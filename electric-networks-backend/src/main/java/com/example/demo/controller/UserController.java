package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<PaginatedResponse<UserDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "15") int perPage) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(userService.getAll(page, perPage))
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
    public ResponseEntity<Response<UserDTO>> add(@RequestBody UserRequest user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return new ResponseEntity<>(
                new Response<>(userService.add(user)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<UserDTO>> update(@PathVariable long id,
                                                    @RequestBody UserRequest user) throws UserException {
        user.setId(id);
        return ResponseEntity.ok(
                new Response<>(userService.update(user))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) {
        userService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
