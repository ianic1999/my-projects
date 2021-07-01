package com.example.demo.service.impl;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.jwt.JwtRequest;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.FileSaver;
import com.example.demo.util.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " doesn't exist"));
    }

    @Override
    public Page<UserDTO> getAll(int page, int size, String order, boolean ascending, String search) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, size)
                : ascending ? PageRequest.of(page - 1, size, Sort.by(order).ascending())
                    : PageRequest.of(page - 1, size, Sort.by(order).descending());
        return userRepository.findByEmailContainsIgnoreCaseAndRole(search, UserRole.USER, pageable).map(User::toDTO);
    }

    @Override
    public UserDTO getById(long id) throws UserException {
        return userRepository.findById(id).orElseThrow(() -> new UserException("User with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    public JwtRequest add(String email, String fullName, MultipartFile image, String password) throws UserException, IOException {
        if (userRepository.findByEmail(email).isPresent())
            throw new UserException("User with email " + email + " already exist");
        String imagePath = FileSaver.save(image, "users", image.getOriginalFilename());
        User user = User.builder()
                .email(email)
                .fullName(fullName)
                .image(imagePath)
                .password(password)
                .build();
        user = userRepository.save(user);
        return JwtRequest.builder()
                .email(user.getEmail())
                .build();
    }

    @Override
    public void remove(long id) throws UserException, IOException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserException("User with id " + id + " doesn't exist"));
        if (user.getRole().equals(UserRole.ADMIN))
            throw new UserException("You can't remove admin users");
        FileSaver.delete(user.getImage());
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDTO update(long id, String email, String fullName, MultipartFile image, String password) throws UserException, IOException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserException("User with id " + id + " doesn't exist"));
        String imagePath = null;
        if (image != null) {
            imagePath = FileSaver.save(image, "users", image.getOriginalFilename());
        }
        if (imagePath != null && (imagePath.endsWith(".png") || imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg")))
            FileSaver.delete(user.getImage());
        if (email != null) {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent() && !optionalUser.get().getEmail().equals(email))
                throw  new UserException("User with email " + email + " already exists");
            user.setEmail(email);
        }
        if (fullName != null)
            user.setFullName(fullName);
        if (imagePath != null)
            user.setImage(imagePath);
        if (password != null)
            user.setPassword(password);
        return user.toDTO();
    }

    @Override
    public UserDTO getCurrentUser() throws UserException {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(user.getUsername()).orElseThrow(() -> new UserException("User with email " + user.getUsername() + " doesn't exist"))
                .toDTO();
    }
}
