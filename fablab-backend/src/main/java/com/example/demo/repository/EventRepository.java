package com.example.demo.repository;

import com.example.demo.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByTitleContainsIgnoreCase(String argument, Pageable pageable);
    Page<Event> findByTitleContainsIgnoreCaseAndDueDateAtAfter(String argument, LocalDateTime now, Pageable pageable);
    Page<Event> findByTitleContainsIgnoreCaseAndDueDateAtBefore(String argument, LocalDateTime now, Pageable pageable);
}
