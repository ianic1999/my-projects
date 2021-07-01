package com.example.demo.service;

import com.example.demo.dto.SettingDTO;
import com.example.demo.model.exception.SettingException;
import org.springframework.data.domain.Page;

public interface SettingService {
    Page<SettingDTO> getAll(int page, int size, String order, boolean ascending, String search);
    Page<SettingDTO> getAllActive(int page, int size, String order, boolean ascending, String search);
    SettingDTO getById(long id) throws SettingException;
    SettingDTO add(SettingDTO dto) throws SettingException;
    SettingDTO update(SettingDTO dto) throws SettingException;
    void remove(long id);
}
