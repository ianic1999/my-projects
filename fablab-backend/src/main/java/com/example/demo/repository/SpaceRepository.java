package com.example.demo.repository;

import com.example.demo.model.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    Page<Space> findByTitle_RoContainsIgnoreCase(String argument, Pageable pageable);
    Page<Space> findByTitle_RuContainsIgnoreCase(String argument, Pageable pageable);
    Page<Space> findByTitle_EnContainsIgnoreCase(String argument, Pageable pageable);
}
