package com.example.demo.service;

import com.example.demo.dto.SpaceDTO;
import com.example.demo.model.exception.SpaceException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SpaceService {
    Page<SpaceDTO> getAll(int page, int size, String order, boolean ascending, String search, String language);
    SpaceDTO getById(long id) throws SpaceException;
    SpaceDTO add(String title, double area, double price, MultipartFile[] images, String description, long userId) throws IOException, UserException;
    SpaceDTO update(long id, String title, String area, String price, MultipartFile[] images, List<Long> deletedImages, String description) throws SpaceException, IOException;
    void remove(long id) throws IOException, SpaceException;
}
