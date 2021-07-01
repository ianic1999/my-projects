package com.example.demo.service;

import com.example.demo.dto.EventDTO;
import com.example.demo.model.exception.EventException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;


public interface EventService {
    Page<EventDTO> getAll(int page, int size, String order, boolean ascending, String search, String upcoming);
    EventDTO getById(long id) throws EventException;
    EventDTO add(String title, String body, String link, String location, LocalDateTime dueDateAt, MultipartFile image, long userId) throws IOException, UserException;
    EventDTO update(long id, String title, String body, String link, String location, LocalDateTime dueDateAt, MultipartFile image) throws EventException, IOException;
    void remove(long id);
}
