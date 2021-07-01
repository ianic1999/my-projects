package com.example.demo.repository;

import com.example.demo.model.Indication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IndicationRepository extends JpaRepository<Indication, Long> {
    Page<Indication> findByCustomer_Id(long customerId, Pageable pageable);
    Page<Indication> findByCustomer_IdAndSelectedAtBetween(long customerId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<Indication> findByCustomer_Id(long customerId);
}
