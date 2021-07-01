package com.example.demo.service.impl;

import com.example.demo.dto.ServiceDTO;
import com.example.demo.model.ServiceSummary;
import com.example.demo.model.ServiceTitle;
import com.example.demo.model.User;
import com.example.demo.model.exception.ServiceException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.ServiceRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ServiceService;
import com.example.demo.util.FileSaver;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    @Override
    public Page<ServiceDTO> getAll(int page, int size, String order, boolean ascending, String search, String language) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, size)
                : ascending ? PageRequest.of(page - 1, size, Sort.by(order).ascending())
                    : PageRequest.of(page - 1, size, Sort.by(order).descending());
        switch (language) {
            case "en":
                return serviceRepository.findByTitle_EnContainsIgnoreCase(search, pageable).map(com.example.demo.model.Service::toDTO);
            case "ru":
                return serviceRepository.findByTitle_RuContainsIgnoreCase(search, pageable).map(com.example.demo.model.Service::toDTO);
            default:
                return serviceRepository.findByTitle_RoContainsIgnoreCase(search, pageable).map(com.example.demo.model.Service::toDTO);
        }
    }

    @Override
    public ServiceDTO getById(long id) throws ServiceException {
        return serviceRepository.findById(id).orElseThrow(() -> new ServiceException("Service with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    @Transactional
    public ServiceDTO add(String title, String summary, MultipartFile image, long userId) throws IOException, UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User with id " + userId + " doesn't exist"));
        String imagePath = FileSaver.save(image, "services", image.getOriginalFilename());
        com.example.demo.model.Service service = com.example.demo.model.Service.builder()
                .image(imagePath)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(user)
                .build();
        service = serviceRepository.save(service);
        ServiceTitle serviceTitle = new Gson().fromJson(title, ServiceTitle.class);
        serviceTitle.setService(service);
        ServiceSummary serviceSummary = new Gson().fromJson(summary, ServiceSummary.class);
        serviceSummary.setService(service);
        service.setTitle(serviceTitle);
        service.setSummary(serviceSummary);
        return service.toDTO();
    }

    @Override
    @Transactional
    public ServiceDTO update(long id, String title, String summary, MultipartFile image) throws IOException, ServiceException {
        com.example.demo.model.Service service = serviceRepository.findById(id).orElseThrow(() -> new ServiceException("Service with id " + id + " doesn't exist"));
        String imagePath = null;
        if (image != null) {
            imagePath = FileSaver.save(image, "services", image.getOriginalFilename());
        }
        if (imagePath != null && (imagePath.endsWith(".png") || imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg")))
            FileSaver.delete(service.getImage());
        if (title != null) {
            ServiceTitle serviceTitle = new Gson().fromJson(title, ServiceTitle.class);
            if (serviceTitle.getRo() != null)
                service.getTitle().setRo(serviceTitle.getRo());
            if (serviceTitle.getRu() != null)
                service.getTitle().setRu(serviceTitle.getRu());
            if (serviceTitle.getEn() != null)
                service.getTitle().setEn(serviceTitle.getEn());
        }
        if (summary != null) {
            ServiceSummary serviceSummary = new Gson().fromJson(summary, ServiceSummary.class);
            if (serviceSummary.getRo() != null)
                service.getSummary().setRo(serviceSummary.getRo());
            if (serviceSummary.getRu() != null)
                service.getSummary().setRu(serviceSummary.getRu());
            if (serviceSummary.getEn() != null)
                service.getSummary().setEn(serviceSummary.getEn());
        }
        if (imagePath != null)
            service.setImage(imagePath);
        service.setUpdatedAt(LocalDateTime.now());
        return service.toDTO();
    }

    @Override
    public void remove(long id) {
        serviceRepository.deleteById(id);
    }
}
