package com.example.demo.service;

import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.ForgotPasswordRequest;
import com.example.demo.dto.request.JwtRequest;
import com.example.demo.dto.request.TokenRequest;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.model.exception.UserException;

public interface AuthenticationService {
    JwtResponse authenticate(JwtRequest request) throws UserException;
    void changePassword(ChangePasswordRequest request) throws UserException;
    void sendPasswordResetLink(String email) throws UserException;
    void changeForgottenPassword(ForgotPasswordRequest request) throws UserException;
    boolean isTokenValid(TokenRequest request) throws UserException;
}
