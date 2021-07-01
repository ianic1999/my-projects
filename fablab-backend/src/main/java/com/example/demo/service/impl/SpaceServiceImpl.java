package com.example.demo.service.impl;

import com.example.demo.dto.SpaceDTO;
import com.example.demo.model.*;
import com.example.demo.model.exception.AlbumException;
import com.example.demo.model.exception.SpaceException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.SpaceImageRepository;
import com.example.demo.repository.SpaceRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SpaceService;
import com.example.demo.util.FileSaver;
import com.example.demo.util.ImageKey;
import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {
    private final SpaceRepository spaceRepository;
    private final SpaceImageRepository spaceImageRepository;
    private final UserRepository userRepository;

    @Override
    public Page<SpaceDTO> getAll(int page, int size, String order, boolean ascending, String search, String language) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, size)
                : ascending ? PageRequest.of(page - 1, size, Sort.by(order).ascending())
                    : PageRequest.of(page - 1, size, Sort.by(order).descending());
        switch (language) {
            case "ru":
                return spaceRepository.findByTitle_RuContainsIgnoreCase(search, pageable).map(Space::toDTO);
            case "en":
                return spaceRepository.findByTitle_EnContainsIgnoreCase(search, pageable).map(Space::toDTO);
            default:
                return spaceRepository.findByTitle_RoContainsIgnoreCase(search, pageable).map(Space::toDTO);
        }
    }

    @Override
    public SpaceDTO getById(long id) throws SpaceException {
        return spaceRepository.findById(id).orElseThrow(() -> new SpaceException("Space with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    @Transactional
    public SpaceDTO add(String title, double area, double price, MultipartFile[] images, String description, long userId) throws IOException, UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User with id " + userId + " doesn't exist"));
        Space space = Space.builder()
                .area(area)
                .price(price)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(user)
                .build();
        space = spaceRepository.save(space);
        SpaceTitle spaceTitle = new Gson().fromJson(title, SpaceTitle.class);
        spaceTitle.setSpace(space);
        SpaceDescription spaceDescription = new Gson().fromJson(description, SpaceDescription.class);
        spaceDescription.setSpace(space);
        space.setTitle(spaceTitle);
        space.setDescription(spaceDescription);
        List<SpaceImage> imagesList = new ArrayList<>();
        for (MultipartFile image: images) {
            String path = FileSaver.save(image, "spaces", image.getOriginalFilename());
            SpaceImage img = SpaceImage.builder()
                    .filename(image.getOriginalFilename())
                    .path(path)
                    .space(space)
                    .build();
            img = spaceImageRepository.save(img);
            imagesList.add(img);
        }
        space.setImages(imagesList);
        return space.toDTO();
    }

    @Override
    @Transactional
    public SpaceDTO update(long id, String title, String area, String price, MultipartFile[] images, List<Long> deletedImages, String description) throws SpaceException, IOException {
        Space space = spaceRepository.findById(id).orElseThrow(() -> new SpaceException("Space with id " + id + " doesn't exist"));
        if (title != null) {
            SpaceTitle spaceTitle = new Gson().fromJson(title, SpaceTitle.class);
            if (spaceTitle.getRo() != null)
                space.getTitle().setRo(spaceTitle.getRo());
            if (spaceTitle.getRu() != null)
                space.getTitle().setRu(spaceTitle.getRu());
            if (spaceTitle.getEn() != null)
                space.getTitle().setEn(spaceTitle.getEn());
        }
        if (area != null)
            space.setArea(Double.parseDouble(area));
        if (price != null) {
            space.setPrice(Double.parseDouble(price));
        }
        if (description != null) {
            SpaceDescription spaceDescription = new Gson().fromJson(description, SpaceDescription.class);
            if (spaceDescription.getRo() != null)
                space.getDescription().setRo(spaceDescription.getRo());
            if (spaceDescription.getRu() != null)
                space.getDescription().setRu(spaceDescription.getRu());
            if (spaceDescription.getEn() != null)
                space.getDescription().setEn(spaceDescription.getEn());
        }
        if (deletedImages != null) {
            for (long imageId : deletedImages) {
                SpaceImage imageToDelete = spaceImageRepository.findById(imageId).orElseThrow(() -> new SpaceException("Image with id " + imageId + " doesn't exist"));
                String filename = imageToDelete.getPath();
                FileSaver.delete(filename);
                space.getImages().remove(imageToDelete);
                spaceImageRepository.deleteById(imageToDelete.getId());
            }
        }
        if (images != null) {
            for (MultipartFile file : images) {
                String path = FileSaver.save(file, "events", file.getOriginalFilename());
                SpaceImage img = spaceImageRepository.save(
                        SpaceImage.builder()
                                .filename(file.getOriginalFilename())
                                .path(path)
                                .space(space)
                                .build()
                );
                space.getImages().add(img);
            }
        }
        space.setUpdatedAt(LocalDateTime.now());
        return space.toDTO();
    }

    @Override
    public void remove(long id) throws IOException, SpaceException {
        Space space = spaceRepository.findById(id).orElseThrow(() -> new SpaceException("Space with id " + id + " doesn't exist"));
        for (SpaceImage img : space.getImages()) {
            FileSaver.delete(img.getPath());
        }
        spaceRepository.deleteById(id);
    }
}
