package com.example.demo.service.impl;

import com.example.demo.config.JwtTokenUtil;
import com.example.demo.dto.jwt.*;
import com.example.demo.model.User;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.UserRepository;
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
    private final UserRepository userRepository;
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
            throw new UserException("User cu email-ul " + email + " nu exista");
        }

        if (passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String role = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UserException("User with email " + userDetails.getUsername() + " doesn't exist")).getRole().getKey();
            String token = jwtTokenUtil.generateToken(userDetails, role, request.isRemember());
            return JwtResponse.builder()
                    .token(token)
                    .build();
        }
        throw new UserException("Parola invalida");
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
            messages.add("User cu email-ul " + email + " nu exista");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            messages.add("Parola gresita");
        if (!request.getPassword().equals(request.getConfirmPassword()))
            messages.add("Parolele nu coincid");
        if (request.getPassword().equals(request.getCurrentPassword()))
            messages.add("Parola veche coincide cu ea noua");
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
            throw new UserException("User cu email-ul " + email + " nu exista");
        }
        String role = userRepository.findByEmail(user.getUsername()).orElseThrow(() -> new UserException("User with email " + user.getUsername() + " doesn't exist")).getRole().getKey();
        String token = jwtTokenUtil.generateToken(user, role,false);
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
            throw new UserException("Parolele nu coincid");
        ((User) user).setPassword(passwordEncoder.encode(request.getPassword()));
    }

    private UserDetails getUserFromToken(String token) throws UserException {
        String email = jwtTokenUtil.getEmailFromToken(token);
        UserDetails user;
        try {
            user = userService.loadUserByUsername(email);
        }
        catch (UsernameNotFoundException e) {
            throw new UserException("Token-ul dvs este invalid");
        }
        if (jwtTokenUtil.isTokenExpired(token))
            throw new UserException("Token-ul dvs este expirat");
        return user;
    }
}
