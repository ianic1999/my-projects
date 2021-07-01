package com.example.demo.service;

import com.example.demo.dto.SettingDTO;
import com.example.demo.model.exception.SettingException;
import org.springframework.data.domain.Page;

public interface SettingService {
    Page<SettingDTO> get(int page, int perPage);
    Page<SettingDTO> getActive(int page, int perPage);
    SettingDTO getById(long id) throws SettingException;
    SettingDTO add(SettingDTO setting) throws SettingException;
    SettingDTO update(SettingDTO setting) throws SettingException;
    void remove(long id);
}
