package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.jwt.JwtRequest;
import com.example.demo.dto.jwt.JwtResponse;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.UserService;
import com.example.demo.util.PasswordUtil;
import com.example.demo.util.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Random;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final MailService mailService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<UserDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "15") int perPage,
                                                             @RequestParam(required = false) String order,
                                                             @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(userService.getAll(page, perPage, order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-"), search))
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
    public ResponseEntity<Response<String>> register(@RequestParam String email,
                                                          @RequestParam String fullName,
                                                          @RequestParam MultipartFile image,
                                                          @RequestParam(required = false) String password) throws IOException, UserException {
        if (password == null)
            password = PasswordUtil.generatePassword();
        JwtRequest request = userService.add(email, fullName, image, passwordEncoder.encode(password));
        request.setPassword(password);
        JwtResponse response = authenticationService.authenticate(request);
        mailService.sendInvitation(email, response.getToken());
        return ResponseEntity.ok(
                new Response<>("User " + email + " was successfully registered.")
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Response<UserDTO>> updatePut(@PathVariable long userId,
                                                       @RequestParam String id,
                                                       @RequestParam String email,
                                                       @RequestParam String fullName,
                                                       @RequestParam(required = false) MultipartFile image,
                                                       @RequestParam(required = false) String password) throws IOException, UserException {
        if (password != null)
            password = passwordEncoder.encode(password);
        return ResponseEntity.ok(
                new Response<>(userService.update(userId, email, fullName, image, password))
        );
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Response<UserDTO>> updatePatch(@PathVariable long userId,
                                                       @RequestParam(required = false) String id,
                                                       @RequestParam(required = false) String email,
                                                       @RequestParam(required = false) String fullName,
                                                       @RequestParam(required = false) MultipartFile image,
                                                       @RequestParam(required = false) String password) throws IOException, UserException {
        if (password != null)
            password = passwordEncoder.encode(password);
        return ResponseEntity.ok(
                new Response<>(userService.update(userId, email, fullName, image, password))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws IOException, UserException {
        userService.remove(id);
        return ResponseEntity.ok().build();
    }
}
