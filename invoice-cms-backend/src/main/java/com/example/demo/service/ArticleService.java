package com.example.demo.service;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.exception.ArticleException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ArticleService {
    Page<ArticleDTO> get(int page, int perPage);
    ArticleDTO getById(long id) throws ArticleException;
    ArticleDTO getByAlias(String alias) throws ArticleException;
    ArticleDTO add(String title,
                   String description,
                   String body,
                   MultipartFile image,
                   String keywords,
                   String youtubeLink,
                   UserDTO currentUser) throws UserException, IOException;
    ArticleDTO update(long id,
                      String title,
                      String description,
                      String body,
                      MultipartFile image,
                      String keywords,
                      String youtubeLink,
                      UserDTO currentUser) throws UserException, ArticleException, IOException;
    void remove(long id, UserDTO currentUser) throws ArticleException, UserException, IOException;
}
