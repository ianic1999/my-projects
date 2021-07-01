package com.example.demo.controller;

import com.example.demo.dto.JwtDTO;
import com.example.demo.dto.RefreshTokenDTO;
import com.example.demo.dto.request.*;
import com.example.demo.dto.response.Response;
import com.example.demo.dto.response.ValidTokenResponse;
import com.example.demo.model.exception.AuthenticationException;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthenticationController {
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Response<JwtDTO>> authenticate(@RequestBody JwtRequest request) throws UserException, AuthenticationException {
        return ResponseEntity.ok(
                new Response<>(authenticationService.authenticate(request, false))
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<Response<JwtDTO>> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) throws AuthenticationException {
        return ResponseEntity.ok(
                new Response<>(authenticationService.refreshAuthentication(refreshTokenDTO))
        );
    }

    @PostMapping("/register/confirm")
    public ResponseEntity<Response<String>> sendRegistrationCode(@RequestBody RegistrationConfirmRequest request) throws AuthenticationException {
        if (!request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"))
            throw new AuthenticationException("password", "Password should be at least 8 characters long, including at least 1 digit, 1 alphabetic character and 1 special character");
        return ResponseEntity.ok(
                new Response<>(authenticationService.sendConfirmRegistrationEmail(request))
        );
    }

    @PostMapping("/register")
    public ResponseEntity<Response<JwtDTO>> register(@RequestParam String email,
                                                     @RequestParam String password,
                                                     @RequestParam String code) throws UserException, AuthenticationException {
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"))
            throw new AuthenticationException("password", "Password should be at least 8 characters long, including at least 1 digit, 1 alphabetic character and 1 special character");
        String encodedPassword = passwordEncoder.encode(password);
        userService.addWithCode(email, encodedPassword, code);
        return ResponseEntity.ok(
                new Response<>(authenticationService.authenticate(
                        new JwtRequest(email, password, false), true
                ))
        );
    }

    @PostMapping("/change_password")
    public ResponseEntity<Response<String>> changePassword(@RequestBody ChangePasswordRequest request) throws UserException, AuthenticationException {
        authenticationService.changePassword(request);
        return ResponseEntity.ok(new Response<>("Your password successfully changed"));
    }

    @PostMapping("/forgot/validate_token")
    public ResponseEntity<ValidTokenResponse> validateToken(@RequestBody ValidTokenRequest request) {
        return ResponseEntity.ok(
                new ValidTokenResponse(authenticationService.isTokenValid(request))
        );
    }

    @PostMapping("/forgot/confirm")
    public ResponseEntity<Response<String>> confirmPassword(@RequestBody ForgotPasswordRequest request) throws UserException, AuthenticationException {
        authenticationService.changeForgottenPassword(request);
        return ResponseEntity.ok(new Response<>("Your password successfully reset"));
    }

    @PostMapping("/forgot")
    public ResponseEntity<Response<String>> sendResetEmail(@RequestBody EmailRequest request) throws UserException {
        authenticationService.sendPasswordResetLink(request.getEmail());
        return ResponseEntity.ok(new Response<>("A message was sent to your email"));
    }
}
