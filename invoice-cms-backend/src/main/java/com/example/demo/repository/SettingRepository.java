package com.example.demo.repository;

import com.example.demo.model.Setting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Page<Setting> findByIsPublicTrue(Pageable pageable);
    Optional<Setting> findByKey(String key);
}
