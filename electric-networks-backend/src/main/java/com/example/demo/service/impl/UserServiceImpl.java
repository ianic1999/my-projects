package com.example.demo.service.impl;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Page<UserDTO> getAll(int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return userRepository.findByRole(UserRole.USER, pageable).map(User::toDTO);
    }

    @Override
    public UserDTO getById(long id) throws UserException {
        return userRepository.findById(id).orElseThrow(() -> new UserException("User with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    public UserDTO add(UserRequest user) {
        User userToAdd = User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        return userRepository.save(userToAdd).toDTO();
    }

    @Override
    @Transactional
    public UserDTO update(UserRequest user) throws UserException {
        User userFromDB = userRepository.findById(user.getId()).orElseThrow(() -> new UserException("User with id " + user.getId() + " doesn't exist"));
        userFromDB.setName(user.getName());
        userFromDB.setEmail(user.getEmail());
        return userFromDB.toDTO();
    }

    @Override
    public void remove(long id) {
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
