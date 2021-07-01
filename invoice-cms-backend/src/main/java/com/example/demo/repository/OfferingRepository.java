package com.example.demo.repository;

import com.example.demo.model.Offering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {
    Page<Offering> findByCreatedBy_EmailAndNameContainsIgnoreCase(Pageable pageable, String email, String argument);
    Page<Offering> findByNameContainsIgnoreCase(Pageable pageable, String argument);
}
