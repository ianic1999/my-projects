package com.example.demo.repository;

import com.example.demo.model.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    Page<Service> findByTitle_RoContainsIgnoreCase(String argument, Pageable pageable);
    Page<Service> findByTitle_RuContainsIgnoreCase(String argument, Pageable pageable);
    Page<Service> findByTitle_EnContainsIgnoreCase(String argument, Pageable pageable);
}
