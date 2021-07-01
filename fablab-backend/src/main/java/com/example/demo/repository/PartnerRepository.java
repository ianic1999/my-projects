package com.example.demo.repository;

import com.example.demo.model.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    Page<Partner> findByNameContainsIgnoreCase(String argument, Pageable pageable);
}
