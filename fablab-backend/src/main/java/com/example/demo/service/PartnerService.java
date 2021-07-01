package com.example.demo.service;

import com.example.demo.dto.PartnerDTO;
import com.example.demo.model.exception.PartnerException;
import com.example.demo.model.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PartnerService {
    Page<PartnerDTO> getAll(int page, int size, String order, boolean ascending, String search);
    PartnerDTO getById(long id) throws PartnerException;
    PartnerDTO add(String name, MultipartFile image, String link, long userId) throws IOException, UserException;
    PartnerDTO update(long id, String name, MultipartFile image, String link) throws PartnerException, IOException;
    void remove(long id) throws PartnerException, IOException;
}
