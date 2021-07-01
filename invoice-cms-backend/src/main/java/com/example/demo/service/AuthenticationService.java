package com.example.demo.service;

import com.example.demo.dto.JwtDTO;
import com.example.demo.dto.RefreshTokenDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.*;
import com.example.demo.model.exception.AuthenticationException;
import com.example.demo.model.exception.UserException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;

public interface AuthenticationService {
    JwtDTO authenticate(JwtRequest request, boolean isRegister) throws UserException, AuthenticationException;
    JwtDTO refreshAuthentication(RefreshTokenDTO refreshTokenDTO) throws AuthenticationException;
    void changePassword(ChangePasswordRequest request) throws UserException, AuthenticationException;
    void sendPasswordResetLink(String email) throws UserException;
    void changeForgottenPassword(ForgotPasswordRequest request) throws UserException, AuthenticationException;
    boolean isTokenValid(ValidTokenRequest request);
    boolean isPasswordCorrect(String email, String password) throws UserException;
    String sendConfirmRegistrationEmail(RegistrationConfirmRequest request) throws AuthenticationException;
}
