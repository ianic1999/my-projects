package com.example.demo.repository;

import com.example.demo.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Page<Invoice> findByCreatedBy_Email(Pageable pageable, String email);
    List<Invoice> findByCreatedBy_Email(String email);
}
