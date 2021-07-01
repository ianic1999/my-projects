package com.example.demo.repository;

import com.example.demo.model.SpaceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceImageRepository extends JpaRepository<SpaceImage, Long> {
}
