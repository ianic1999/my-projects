package com.example.demo.service;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.model.exception.ArticleException;
import com.example.demo.model.exception.CategoryException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ArticleService {
    Page<ArticleDTO> getAll(int page, int size, String order, boolean ascending, String search);
    ArticleDTO getById(long id) throws ArticleException;
    ArticleDTO add(String title, String body, MultipartFile image, long categoryId, long userId) throws CategoryException, IOException, UserException;
    ArticleDTO update(long id, String title, String body, MultipartFile image, long categoryId) throws CategoryException, ArticleException, IOException;
    void remove(long id) throws ArticleException, IOException;
}
