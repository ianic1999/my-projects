package com.example.demo.repository;

import com.example.demo.model.InvoiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceOfferingRepository extends JpaRepository<InvoiceOffering, Long> {
}
