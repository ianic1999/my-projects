package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.jwt.JwtRequest;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService extends UserDetailsService {
    Page<UserDTO> getAll(int page, int size, String order, boolean ascending, String search);
    UserDTO getById(long id) throws UserException;
    JwtRequest add(String email, String fullName, MultipartFile image, String password) throws UserException, IOException;
    void remove(long id) throws UserException, IOException;
    UserDTO update(long id, String email, String fullName, MultipartFile image, String password) throws UserException, IOException;
    UserDTO getCurrentUser() throws UserException;
}
