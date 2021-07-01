package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.*;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.dto.response.TokenResponse;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Response<JwtResponse>> authenticate(@RequestBody JwtRequest request) throws UserException {
        return ResponseEntity.ok(
                new Response<>(authenticationService.authenticate(request))
        );
    }

    @PostMapping("/change_password")
    public ResponseEntity<Response<String>> changePassword(@RequestBody ChangePasswordRequest request) throws UserException {
        authenticationService.changePassword(request);
        return ResponseEntity.ok(new Response<>("Your password successfully changed"));
    }

    @PostMapping("/forgot/validate_token")
    public ResponseEntity<TokenResponse> validateToken(@RequestBody TokenRequest request) throws UserException {
        return ResponseEntity.ok(
            new TokenResponse(authenticationService.isTokenValid(request))
        );
    }

    @PostMapping("/forgot/confirm")
    public ResponseEntity<Response<String>> confirmPassword(@RequestBody ForgotPasswordRequest request) throws UserException {
        authenticationService.changeForgottenPassword(request);
        return ResponseEntity.ok(new Response<>("Your password successfully reset"));
    }

    @PostMapping("/forgot")
    public ResponseEntity<Response<String>> sendResetEmail(@RequestBody EmailRequest request) throws UserException {
        authenticationService.sendPasswordResetLink(request.getEmail());
        return ResponseEntity.ok(new Response<>("A message was sent to your email"));
    }
}
