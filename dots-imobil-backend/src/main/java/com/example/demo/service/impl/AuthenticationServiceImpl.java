package com.example.demo.service.impl;

import com.example.demo.config.JwtTokenUtil;
import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.ForgotPasswordRequest;
import com.example.demo.dto.request.JwtRequest;
import com.example.demo.dto.request.TokenRequest;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.model.User;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.UserService;
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
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

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
            String role = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UserException("User with email " + userDetails.getUsername() + " doesn't exist")).getRole().getKey();
            String token = jwtTokenUtil.generateToken(userDetails, role, request.isRemember());
            return JwtResponse.builder()
                    .token(token)
                    .build();
        }
        throw new UserException("Invalid password");
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

    }

    @Override
    @Transactional
    public void changeForgottenPassword(ForgotPasswordRequest request) throws UserException {

    }

    @Override
    public boolean isTokenValid(TokenRequest request) throws UserException {
        return false;
    }
}
