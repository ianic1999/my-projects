package com.example.demo.repository;

import com.example.demo.model.Estate;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.ELState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstateRepository extends JpaRepository<Estate, Long>, JpaSpecificationExecutor {
    List<Estate> findByStreetContainsIgnoreCaseOrTitleContainsIgnoreCaseOrDescriptionContainsIgnoreCase(String street, String title, String description);
}
