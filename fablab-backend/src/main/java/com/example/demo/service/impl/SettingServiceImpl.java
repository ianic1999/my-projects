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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final SettingRepository settingRepository;

    @Override
    public Page<SettingDTO> getAll(int page, int size, String order, boolean ascending, String search) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, size)
                : ascending ? PageRequest.of(page - 1, size, Sort.by(order).ascending())
                    : PageRequest.of(page - 1, size, Sort.by(order).descending());
        return settingRepository.findByKeyContainsIgnoreCase(search, pageable).map(Setting::toDTO);
    }

    @Override
    public Page<SettingDTO> getAllActive(int page, int size, String order, boolean ascending, String search) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, size)
                : ascending ? PageRequest.of(page - 1, size, Sort.by(order).ascending())
                : PageRequest.of(page - 1, size, Sort.by(order).descending());
        return settingRepository.findByIsPublicTrue(pageable).map(Setting::toDTO);
    }

    @Override
    public SettingDTO getById(long id) throws SettingException {
        return settingRepository.findById(id).orElseThrow(() -> new SettingException("Setting with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    @Transactional
    public SettingDTO add(SettingDTO dto) throws SettingException {
        if (settingRepository.findByKey(dto.getKey()).isPresent())
            throw new SettingException("Setting with key " + dto.getKey() + " already exist");
        Setting setting = Setting.builder()
                .key(dto.getKey())
                .isPublic(dto.isPublic())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        setting = settingRepository.save(setting);
        setting.setValue(
                SettingValue.builder()
                        .setting(setting)
                        .en(dto.getValue().getEn())
                        .ro(dto.getValue().getRo())
                        .ru(dto.getValue().getRu())
                        .build()
        );
        return setting.toDTO();
    }

    @Override
    @Transactional
    public SettingDTO update(SettingDTO dto) throws SettingException {
        Setting setting = settingRepository.findById(dto.getId()).orElseThrow(() -> new SettingException("Setting with id " + dto.getKey() + " doesn't exist"));
        if (dto.getKey() != null && settingRepository.findByKey(dto.getKey()).isPresent() && !setting.getKey().equals(dto.getKey()))
            throw new SettingException("Setting with key " + dto.getKey() + " already exists");
        if (dto.getKey() != null)
            setting.setKey(dto.getKey());
        setting.setPublic(dto.isPublic());
        if (dto.getValue() != null && dto.getValue().getRo() != null)
            setting.getValue().setRo(dto.getValue().getRo());
        if (dto.getValue() != null && dto.getValue().getEn() != null)
            setting.getValue().setEn(dto.getValue().getEn());
        if (dto.getValue() != null && dto.getValue().getRu() != null)
            setting.getValue().setRu(dto.getValue().getRu());
        setting.setUpdatedAt(LocalDateTime.now());
        return setting.toDTO();
    }

    @Override
    public void remove(long id) {
        settingRepository.deleteById(id);
    }
}
