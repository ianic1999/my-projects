package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService extends UserDetailsService {
    Page<UserDTO> get(int page, int perPage);
    UserDTO getById(long id) throws UserException;
    UserDTO add(String email,
                String fullName,
                String password,
                MultipartFile image) throws UserException, IOException;
    UserDTO update(long id,
                   String email,
                   String fullName,
                   String password,
                   MultipartFile image) throws UserException, IOException;
    void remove(long id) throws UserException, IOException;
    UserDTO getCurrentUser() throws UserException;
}
