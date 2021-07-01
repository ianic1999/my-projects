package com.example.demo.service;

import com.example.demo.dto.jwt.*;
import com.example.demo.model.exception.UserException;

public interface AuthenticationService {
    JwtResponse authenticate(JwtRequest request) throws UserException;
    void changePassword(ChangePasswordRequest request) throws UserException;
    void sendPasswordResetLink(String email) throws UserException;
    void changeForgottenPassword(ForgotPasswordRequest request) throws UserException;
    boolean isTokenValid(TokenRequest request) throws UserException;
}
