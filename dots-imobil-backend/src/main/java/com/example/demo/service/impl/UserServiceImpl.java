package com.example.demo.service.impl;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.model.User;
import com.example.demo.model.enums.UserRole;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.FileSaver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

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
    public UserDTO add(String email,
                       String fullName,
                       String password,
                       MultipartFile image) throws UserException, IOException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserException("User with email " + email + " already exists");
        }
        String imagePath = null;
        if (image != null) {
            imagePath = FileSaver.save(image, "users", image.getOriginalFilename());
        }
        User userToAdd = User.builder()
                .email(email)
                .fullName(fullName)
                .password(password)
                .image(imagePath)
                .build();
        return userRepository.save(userToAdd).toDTO();
    }

    @Override
    @Transactional
    public UserDTO update(long id,
                          String email,
                          String fullName,
                          String password,
                          MultipartFile image) throws UserException, IOException {
        User userFromDB = userRepository.findById(id).orElseThrow(() -> new UserException("User with id " + id + " doesn't exist"));
        String imagePath = null;
        if (image != null) {
            if (userFromDB.getImage() != null) {
                FileSaver.delete(userFromDB.getImage());
            }
            imagePath = FileSaver.save(image, "users", image.getOriginalFilename());
        }
        if (email != null) {
            if (userRepository.findByEmail(email).isPresent() && !userRepository.findByEmail(email).get().getEmail().equals(userFromDB.getEmail()))
                throw new UserException("User with email " + email + " already exists");
            userFromDB.setEmail(email);
        }
        if (fullName != null) {
            userFromDB.setFullName(fullName);
        }
        if (imagePath != null) {
            userFromDB.setImage(imagePath);
        }
        return userFromDB.toDTO();
    }

    @Override
    public void remove(long id) throws UserException, IOException {
        User userFromDB = userRepository.findById(id).orElseThrow(() -> new UserException("User with id " + id + " doesn't exist"));
        if (userFromDB.getImage() != null)
            FileSaver.delete(userFromDB.getImage());
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByEmail(s).orElseThrow(() -> new UsernameNotFoundException("User with email " + s + " doesn't exist"));
    }

    @Override
    public UserDTO getCurrentUser() throws UserException {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(user.getUsername()).orElseThrow(() -> new UserException("User with email " + user.getUsername() + " doesn't exist"))
                .toDTO();
    }
}
