package com.example.demo.service;

import com.example.demo.dto.AlbumDTO;
import com.example.demo.dto.ResponsePair;
import com.example.demo.model.exception.AlbumException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AlbumService {
    Page<AlbumDTO> getAll(int page, int size, String order, boolean ascending, String search);
    Page<AlbumDTO> getAllActive(int page, int size, String order, boolean ascending, String search);
    AlbumDTO getById(long id) throws AlbumException;
    ResponsePair<AlbumDTO> add(MultipartFile coverImage, MultipartFile[] images, String isPublic, long userId) throws IOException, UserException;
    void remove(long id) throws AlbumException, IOException;
    ResponsePair<AlbumDTO> update(long id, MultipartFile coverImage, MultipartFile[] image, List<Long> deletedImages, String isPublic) throws AlbumException, IOException;
}
