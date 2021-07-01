package com.example.demo.service.impl;

import com.example.demo.dto.SettingDTO;
import com.example.demo.model.Setting;
import com.example.demo.model.SettingValue;
import com.example.demo.model.exception.SettingException;
import com.example.demo.repository.SettingRepository;
import com.example.demo.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final SettingRepository settingRepository;

    @Override
    public Page<SettingDTO> get(int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return settingRepository.findAll(pageable).map(Setting::toDTO);
    }

    @Override
    public Page<SettingDTO> getActive(int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return settingRepository.findByIsPublicTrue(pageable).map(Setting::toDTO);
    }

    @Override
    public SettingDTO getById(long id) throws SettingException {
        return settingRepository.findById(id).orElseThrow(() -> new SettingException("Setting with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    @Transactional
    public SettingDTO add(SettingDTO setting) throws SettingException {
        if (settingRepository.findByKey(setting.getKey()).isPresent())
            throw new SettingException("Setting with key " + setting.getKey() + " already exists");
        Setting settingToAdd = Setting.builder()
                .key(setting.getKey())
                .isPublic(setting.isPublic())
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .updatedAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        settingToAdd = settingRepository.save(settingToAdd);
        settingToAdd.setValue(SettingValue.builder()
                                .en(setting.getValue().getEn())
                                .ru(setting.getValue().getRu())
                                .ro(setting.getValue().getRo())
                                .de(setting.getValue().getDe())
                                .es(setting.getValue().getEs())
                                .setting(settingToAdd)
                                .build()
        );
        return settingToAdd.toDTO();
    }

    @Override
    @Transactional
    public SettingDTO update(SettingDTO setting) throws SettingException {
        Setting settingFromDB = settingRepository.findById(setting.getId()).orElseThrow(() -> new SettingException("Setting with id " + setting.getKey() + " doesn't exist"));
        if (setting.getKey() != null && settingRepository.findByKey(setting.getKey()).isPresent() && !settingFromDB.getKey().equals(setting.getKey()))
            throw new SettingException("Setting with key " + setting.getKey() + " already exists");
        if (setting.getKey() != null)
            settingFromDB.setKey(setting.getKey());
        settingFromDB.setPublic(setting.isPublic());
        if (setting.getValue() != null && setting.getValue().getRo() != null)
            settingFromDB.getValue().setRo(setting.getValue().getRo());
        if (setting.getValue() != null && setting.getValue().getEn() != null)
            settingFromDB.getValue().setEn(setting.getValue().getEn());
        if (setting.getValue() != null && setting.getValue().getRu() != null)
            settingFromDB.getValue().setRu(setting.getValue().getRu());
        settingFromDB.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return settingFromDB.toDTO();
    }

    @Override
    public void remove(long id) {
        settingRepository.deleteById(id);
    }
}
