package com.example.demo.repository;

import com.example.demo.model.RegistrationConfirm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationConfirmRepository extends JpaRepository<RegistrationConfirm, Long> {
    Optional<RegistrationConfirm> findByEmailAndCode(String email, String code);
    Optional<RegistrationConfirm> findByEmail(String email);
}
