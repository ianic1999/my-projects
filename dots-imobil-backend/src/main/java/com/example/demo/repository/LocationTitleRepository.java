package com.example.demo.repository;

import com.example.demo.model.LocationTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationTitleRepository extends JpaRepository<LocationTitle, Long> {
}
