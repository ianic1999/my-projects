package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    Page<UserDTO> getAll(int page, int perPage);
    UserDTO getById(long id) throws UserException;
    UserDTO add(UserRequest user);
    UserDTO update(UserRequest user) throws UserException;
    void remove(long id);
    UserDTO getCurrentUser() throws UserException;
}
