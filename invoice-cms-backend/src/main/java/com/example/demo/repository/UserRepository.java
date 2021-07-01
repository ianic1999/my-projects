package com.example.demo.repository;

import com.example.demo.model.User;
import com.example.demo.model.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Page<User> findByRole(Pageable pageable, UserRole role);
    boolean existsByEmail(String email);
}
