package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.enums.UserProvider;
import com.example.demo.model.enums.UserRole;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<PaginatedResponse<UserDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "15") int perPage) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(userService.get(page, perPage))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<UserDTO>> getById(@PathVariable long id) throws UserException {
        return ResponseEntity.ok(
                new Response<>(userService.getById(id))
        );
    }

    @GetMapping("/me")
    public ResponseEntity<Response<UserDTO>> getCurrentUser() throws UserException {
        return ResponseEntity.ok(
                new Response<>(userService.getCurrentUser())
        );
    }

    @PostMapping
    public ResponseEntity<Response<UserDTO>> add(@RequestParam String email,
                                                 @RequestParam String password) throws UserException {
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"))
            throw new UserException("password", "Password should be at least 8 characters long, including at least 1 digit, 1 alphabetic character and 1 special character");
        password = passwordEncoder.encode(password);
        return new ResponseEntity<>(
                new Response<>(userService.addWithoutCode(email, password, null)),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Response<UserDTO>> updatePatch(@PathVariable long userId,
                                                         @RequestParam(required = false) Long id,
                                                         @RequestParam(required = false) String firstName,
                                                         @RequestParam(required = false) String lastName,
                                                         @RequestParam(required = false) String email,
                                                         @RequestParam(required = false) MultipartFile image,
                                                         @RequestParam(required = false) String currentPassword,
                                                         @RequestParam(required = false) String isRegister,
                                                         @RequestParam(required = false) String type) throws UserException, IOException {
        if (!userService.getCurrentUser().getProvider().equals(UserProvider.LOCAL.getKey()))
            return ResponseEntity.ok(
                    new Response<>(userService.update(userId, firstName, lastName, email, image, type))
            );
        if (isRegister != null || userService.getCurrentUser().getRole().equals(UserRole.ADMIN.getKey())) {
            return ResponseEntity.ok(
                    new Response<>(userService.update(userId, firstName, lastName, email, image, type))
            );
        }
        if (authenticationService.isPasswordCorrect(userService.getCurrentUser().getEmail(), currentPassword)) {
            return ResponseEntity.ok(
                    new Response<>(userService.update(userId, firstName, lastName, email, image, type))
            );
        } else
            throw new UserException("Wrong password");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws IOException, UserException {
        userService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
