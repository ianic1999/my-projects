package com.example.demo.repository;

import com.example.demo.model.EstateImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstateImageRepository extends JpaRepository<EstateImage, Long> {
}
