package com.example.demo.service.impl;

import com.example.demo.config.JwtTokenUtil;
import com.example.demo.dto.JwtDTO;
import com.example.demo.dto.RefreshTokenDTO;
import com.example.demo.dto.request.*;
import com.example.demo.model.RegistrationConfirm;
import com.example.demo.model.User;
import com.example.demo.model.enums.UserProvider;
import com.example.demo.model.exception.AuthenticationException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.RegistrationConfirmRepository;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.UserService;
import com.example.demo.util.aws.AwsSESService;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AwsSESService sesService;
    private final RegistrationConfirmRepository registrationConfirmRepository;

    @Override
    public JwtDTO authenticate(JwtRequest request, boolean isRegister) throws AuthenticationException {
        String email = request.getEmail();
        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            throw new AuthenticationException("email", "User with email " + email + " doesn't exist");
        }
        if (((User) userDetails).getProvider() != null && ((User) userDetails).getProvider().equals(UserProvider.GOOGLE))
            throw new AuthenticationException("You should use social authentication");
        if (passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
            User user = (User) userDetails;
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = jwtTokenUtil.generateToken(userDetails, user.getRole().getKey(), request.isRemember(), isRegister);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
            return JwtDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        throw new AuthenticationException("password", "Invalid password");
    }

    @Override
    public JwtDTO refreshAuthentication(RefreshTokenDTO refreshTokenDTO) throws AuthenticationException {
        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(jwtTokenUtil.getEmailFromToken(refreshTokenDTO.getRefreshToken()));
        } catch (UsernameNotFoundException e) {
            throw new AuthenticationException("non_field", "Token is invalid");
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException("non_field", "Your session is expired");
        }
        if (jwtTokenUtil.isTokenValid(refreshTokenDTO.getRefreshToken(), userDetails))
            return JwtDTO.builder()
                        .accessToken(jwtTokenUtil.generateToken(userDetails, ((User) userDetails).getRole().getKey(), false, false))
                        .refreshToken(refreshTokenDTO.getRefreshToken())
                        .build();
        throw new AuthenticationException("non_field", "Your session is expired");
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) throws UserException, AuthenticationException {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getUsername();
        try {
            user = userService.loadUserByUsername(email);
        }
        catch (UsernameNotFoundException e) {
            throw new UserException("email", "User with email " + email + " doesn't exist");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new AuthenticationException("currentPassword", "Wrong current password");
        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new AuthenticationException("confirmPassword", "The passwords don't match");
        if (request.getPassword().equals(request.getCurrentPassword()))
            throw new AuthenticationException("password", "New password and the current one are the same");
        if (!request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"))
            throw new AuthenticationException("password", "Password should be at least 8 characters long, including at least 1 digit, 1 alphabetic character and 1 special character");
        ((User) user).setPassword(passwordEncoder.encode(request.getPassword()));
    }

    @Override
    public void sendPasswordResetLink(String email) throws UserException {
        UserDetails user;
        try {
            user = userService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            throw new UserException("email", "User with email " + email + " doesn't exist");
        }
        String token = jwtTokenUtil.generateToken(user, ((User)user).getRole().getKey(), false, false);
        sesService.sendResetLink(email, ((User) user).getFirstName(), token);
    }

    @Override
    @Transactional
    public void changeForgottenPassword(ForgotPasswordRequest request) throws UserException, AuthenticationException {
        UserDetails user = getUserFromToken(request.getToken());
        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new AuthenticationException("confirmPassword", "The passwords don't match");
        if (!request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"))
            throw new AuthenticationException("password", "Password should be at least 8 characters long, including at least 1 digit, 1 alphabetic character and 1 special character");
        ((User) user).setPassword(passwordEncoder.encode(request.getPassword()));
    }

    @Override
    public boolean isTokenValid(ValidTokenRequest request) {
        UserDetails user;
        try {
            user = getUserFromToken(request.getToken());
        } catch (UserException | ExpiredJwtException | SignatureException | AuthenticationException e) {
            return false;
        }
        return !jwtTokenUtil.isTokenExpired(request.getToken()) && jwtTokenUtil.isTokenValid(request.getToken(), user);
    }

    private UserDetails getUserFromToken(String token) throws UserException, AuthenticationException {
        String email = jwtTokenUtil.getEmailFromToken(token);
        UserDetails user;
        try {
            user = userService.loadUserByUsername(email);
        }
        catch (UsernameNotFoundException e) {
            throw new AuthenticationException("Your token is invalid");
        }
        if (jwtTokenUtil.isTokenExpired(token))
            throw new AuthenticationException("Your token is expired");
        return user;
    }

    @Override
    public boolean isPasswordCorrect(String email, String password) throws UserException {
        User user;
        try {
            user = (User)userService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            throw new UserException("User with email " + email + " doesn't exist");
        }
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public String sendConfirmRegistrationEmail(RegistrationConfirmRequest request) throws AuthenticationException {
        try {
            userService.loadUserByUsername(request.getEmail());
            throw new AuthenticationException("email", "There is already an user with email " + request.getEmail());
        } catch (UsernameNotFoundException ignored) { }
        String code = generateCode();
        RegistrationConfirm confirm;
        if (registrationConfirmRepository.findByEmail(request.getEmail()).isPresent()) {
            confirm = registrationConfirmRepository.findByEmail(request.getEmail()).get();
        } else {
            confirm = RegistrationConfirm.builder()
                    .email(request.getEmail())
                    .build();
        }
        sesService.sendRegistrationCode(request.getEmail(), code);
        confirm.setCode(code);
        confirm.setDeadline(LocalDateTime.now().plusDays(1));
        registrationConfirmRepository.save(confirm);
        return "Registration code was sent to " + request.getEmail();
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(String.valueOf((int) (Math.random() * Math.pow(10, 5))));
        while (code.length() < 5)
            code.insert(0, "0");
        return code.toString();
    }
}
