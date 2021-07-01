package com.example.demo.service;

import com.example.demo.dto.TestimonialDTO;
import com.example.demo.model.exception.TestimonialException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TestimonialService {
    Page<TestimonialDTO> getAll(int page, int size, String order, boolean ascending, String search);
    TestimonialDTO getById(long id) throws TestimonialException;
    TestimonialDTO add(String name, String jobTitle, MultipartFile image, String message, long userId) throws IOException, UserException;
    TestimonialDTO update(long id, String name, String jobTitle, MultipartFile image, String message) throws TestimonialException, IOException;
    void remove(long id) throws IOException, TestimonialException;
}
