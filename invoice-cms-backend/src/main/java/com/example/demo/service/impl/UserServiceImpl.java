package com.example.demo.service.impl;

import com.example.demo.config.JwtTokenUtil;
import com.example.demo.dto.JwtDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.Company;
import com.example.demo.model.RegistrationConfirm;
import com.example.demo.model.User;
import com.example.demo.model.enums.UserProvider;
import com.example.demo.model.enums.UserRole;
import com.example.demo.model.enums.UserType;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.RegistrationConfirmRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.aws.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final AwsS3Service awsService;
    private final RegistrationConfirmRepository registrationConfirmRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByEmail(s).orElseThrow(() -> new UsernameNotFoundException("User with email " + s + " doesn't exist"));
    }

    @Override
    public Page<UserDTO> get(int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return userRepository.findByRole(pageable, UserRole.USER).map(User::toDTO);
    }

    @Override
    public UserDTO getById(long id) throws UserException {
        return userRepository.findById(id).orElseThrow(() -> new UserException("User with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    @Transactional
    public UserDTO addWithCode(String email, String password, String code) throws UserException {
        if (registrationConfirmRepository.findByEmailAndCode(email, code).isPresent()) {
            RegistrationConfirm confirm = registrationConfirmRepository.findByEmailAndCode(email, code).get();
            if (confirm.getDeadline().isBefore(LocalDateTime.now()))
                throw new UserException("code", "Your code is expired");
        } else throw new UserException("code", "Invalid or expired code " + code + " for email " + email);
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserException("User with email " + email + " already exists");
        }
        User user = User.builder()
                .email(email)
                .password(password)
                .companies(new ArrayList<>())
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .updatedAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        user = userRepository.save(user);
        return user.toDTO();
    }

    @Override
    public UserDTO addWithoutCode(String email, String password, UserProvider provider) throws UserException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserException("User with email " + email + " already exists");
        }
        User user = User.builder()
                .email(email)
                .password(password)
                .companies(new ArrayList<>())
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .updatedAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        if (provider != null) {
            user.setProvider(provider);
        }
        user = userRepository.save(user);
        return user.toDTO();
    }

    @Override
    @Transactional
    public UserDTO update(long id, String firstName, String lastName, String email, MultipartFile image, String type) throws UserException, IOException {
        Company company;
        String imagePath = null;
        User user = userRepository.findById(id).orElseThrow(() -> new UserException("User with id " + id + " doesn't exist"));
        if (image != null) {
            if (user.getImage() != null)
                awsService.deleteFile("users/" + user.getId() + "/" + user.getImage());
            imagePath = awsService.uploadFile(image, "users/" + user.getId() + "/", image.getOriginalFilename());
        }
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (imagePath != null) {
            user.setImage(imagePath);
        }
        if (email != null) {
            if (userRepository.findByEmail(email).isPresent() && !email.equals(user.getEmail())) {
                throw new UserException("User with email " + email + " already exists");
            }
            user.setEmail(email);
        }
        if (type != null) {
            user.setType(UserType.valueOf(type));
        }
        user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return user.toDTO();
    }

    @Override
    public void remove(long id) throws UserException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserException("User with id " + id + " doesn't exist"));
        if (user.getImage() != null)
            awsService.deleteFile("users/" + user.getId() + "/" + user.getImage());
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public JwtDTO authenticateSocial(OAuth2User oAuth2User) throws UserException, IOException {
        String email = oAuth2User.getAttribute("email");
        boolean isRegister = false;
        User user;
        if (userRepository.existsByEmail(email))
            user = userRepository.findByEmail(email).get();
        else
            user = null;
        if (user == null ) {
            isRegister = true;
            String firstName = oAuth2User.getAttribute("family_name");
            String lastName = oAuth2User.getAttribute("given_name");
            UserDTO userDTO = this.addWithoutCode(email, "Social123!", UserProvider.GOOGLE);
            this.update(userDTO.getId(), firstName, lastName, email, null, null);
        } else {
            if (user.getProvider().equals(UserProvider.LOCAL))
                throw new UserException("Feels your social identity is not linked to your account, please try to login by providing your email and password instead.");
        }
        UserDetails userDetails = this.loadUserByUsername(email);
        return JwtDTO.builder()
                .accessToken(jwtTokenUtil.generateToken(userDetails, ((User) userDetails).getRole().getKey(), false, isRegister))
                .refreshToken(jwtTokenUtil.generateRefreshToken(userDetails))
                .build();
    }

    @Override
    public UserDTO getCurrentUser() throws UserException {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(user.getUsername()).orElseThrow(() -> new UserException("User with email " + user.getUsername() + " doesn't exist"))
                .toDTO();

    }
}
