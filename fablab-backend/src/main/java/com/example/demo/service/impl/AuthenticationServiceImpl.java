package com.example.demo.service.impl;

import com.example.demo.config.JwtTokenUtil;
import com.example.demo.dto.jwt.*;
import com.example.demo.model.User;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.UserService;
import com.example.demo.util.mail.MailService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final MailService mailService;

    @Override
    public JwtResponse authenticate(JwtRequest request) throws UserException {
        String email = request.getEmail();
        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(email);
        }
        catch (UsernameNotFoundException e) {
            throw new UserException("User with email " + email + " doesn't exist");
        }

        if (passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenUtil.generateToken(userDetails, request.isRemember());
            return JwtResponse.builder()
                    .token(token)
                    .build();
        }
        throw new UserException("Invalid credentials");
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) throws UserException {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getUsername();
        List<String> messages = new ArrayList<>();
        try {
            user = userService.loadUserByUsername(email);
        }
        catch (UsernameNotFoundException e) {
            messages.add("User with email " + email + " doesn't exist");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            messages.add("Wrong password");
        if (!request.getPassword().equals(request.getConfirmPassword()))
            messages.add("The passwords don't match");
        if (request.getPassword().equals(request.getCurrentPassword()))
            messages.add("New password and current one are the same");
        if (messages.size() > 0)
            throw new UserException(messages);
        ((User) user).setPassword(passwordEncoder.encode(request.getPassword()));
    }

    @Override
    public void sendPasswordResetLink(String email) throws UserException {
        UserDetails user;
        try {
            user = userService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            throw new UserException("User with email " + email + " doesn't exist");
        }
        String token = jwtTokenUtil.generateToken(user, false);
        mailService.sendResetLink(email, token);
    }

    @Override
    public boolean isTokenValid(TokenRequest request) {
        UserDetails user = null;
        try {
            user = getUserFromToken(request.getToken());
        } catch (UserException | ExpiredJwtException | SignatureException e) {
            return false;
        }
        return !jwtTokenUtil.isTokenExpired(request.getToken()) && jwtTokenUtil.isTokenValid(request.getToken(), user);
    }

    @Override
    @Transactional
    public void changeForgottenPassword(ForgotPasswordRequest request) throws UserException {
        UserDetails user = getUserFromToken(request.getToken());
        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new UserException("The passwords don't match");
        ((User) user).setPassword(passwordEncoder.encode(request.getPassword()));
    }

    private UserDetails getUserFromToken(String token) throws UserException {
        String email = jwtTokenUtil.getEmailFromToken(token);
        UserDetails user;
        try {
            user = userService.loadUserByUsername(email);
        }
        catch (UsernameNotFoundException e) {
            throw new UserException("Your token is invalid");
        }
        if (jwtTokenUtil.isTokenExpired(token))
            throw new UserException("Your token is expired");
        return user;
    }
}
