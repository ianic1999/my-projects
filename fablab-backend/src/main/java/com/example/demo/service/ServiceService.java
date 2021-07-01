package com.example.demo.service;

import com.example.demo.dto.ServiceDTO;
import com.example.demo.model.exception.ServiceException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ServiceService {
    Page<ServiceDTO> getAll(int page, int size, String order, boolean ascending, String search, String language);
    ServiceDTO getById(long id) throws ServiceException;
    ServiceDTO add(String title, String summary, MultipartFile image, long userId) throws IOException, UserException;
    ServiceDTO update(long id, String title, String summary, MultipartFile image) throws IOException, ServiceException;
    void remove(long id);
}
