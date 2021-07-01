package com.example.demo.service;

import com.example.demo.dto.CompanyDTO;
import com.example.demo.dto.JwtDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.enums.UserProvider;
import com.example.demo.model.exception.CompanyException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService extends UserDetailsService {
    Page<UserDTO> get(int page, int perPage);
    UserDTO getById(long id) throws UserException;
    UserDTO addWithCode(String email, String password, String code) throws UserException;
    UserDTO addWithoutCode(String email, String password, UserProvider provider) throws UserException;
    UserDTO update(long id,
                   String firstName,
                   String lastName,
                   String email,
                   MultipartFile image,
                   String type) throws UserException, IOException;
    void remove(long id) throws UserException, IOException;
    UserDTO getCurrentUser() throws UserException;
    JwtDTO authenticateSocial(OAuth2User user) throws UserException, IOException;
}
