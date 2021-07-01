package com.example.demo.service.impl;

import com.example.demo.dto.AlbumDTO;
import com.example.demo.dto.ResponsePair;
import com.example.demo.model.Album;
import com.example.demo.model.Image;
import com.example.demo.model.User;
import com.example.demo.model.exception.AlbumException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AlbumService;
import com.example.demo.util.FileSaver;
import com.example.demo.util.ImageKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    @Override
    public Page<AlbumDTO> getAll(int page, int size, String order, boolean ascending, String search) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, size)
                : ascending ? PageRequest.of(page - 1, size, Sort.by(order).ascending())
                    : PageRequest.of(page - 1, size, Sort.by(order).descending());
        return albumRepository.findAll(pageable).map(Album::toDTO);
    }

    @Override
    public Page<AlbumDTO> getAllActive(int page, int size, String order, boolean ascending, String search) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, size)
                : ascending ? PageRequest.of(page - 1, size, Sort.by(order).ascending())
                : PageRequest.of(page - 1, size, Sort.by(order).descending());
        return albumRepository.findByIsPublicTrue(pageable).map(Album::toDTO);
    }

    @Override
    public AlbumDTO getById(long id) throws AlbumException {
        return albumRepository.findById(id).orElseThrow(() -> new AlbumException("Album with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    @Transactional
    public ResponsePair<AlbumDTO> add(MultipartFile coverImage, MultipartFile[] images, String isPublic, long userId) throws IOException, UserException {
        List<String> exceptions = new ArrayList<>();
        String coverImagePath = FileSaver.save(coverImage, "covers", coverImage.getOriginalFilename());
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User with id " + userId + " doesn't exist"));
        Album album = Album.builder()
                .coverImage(coverImagePath)
                .images(new ArrayList<>())
                .isPublic(isPublic.equals("true"))
                .createdBy(user)
                .build();
        album = albumRepository.save(album);
        long id = album.getId();
        for (MultipartFile image : images) {
            if (image != null && image.getOriginalFilename() != null && image.getOriginalFilename().matches(".+\\.(png|jpg|jpeg|webp|jpeg2000)")) {
                String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
                String path = FileSaver.save(image, "images", ImageKey.ORIGINAL.getKey() + id + extension);
                String originalFilename = path != null ? path.substring(path.lastIndexOf("/") + 1) : null;
                Image img = imageRepository.save(
                        Image.builder()
                                .filename(originalFilename)
                                .key(ImageKey.ORIGINAL)
                                .path(path)
                                .album(album)
                                .build()
                );
                album.getImages().add(img);
                path = FileSaver.saveResized("images", originalFilename, ImageKey.THUMBNAIL.getKey() + id + extension);
                originalFilename = path != null ? path.substring(path.lastIndexOf("/") + 1) : null;
                img = imageRepository.save(
                        Image.builder()
                                .filename(originalFilename)
                                .key(ImageKey.THUMBNAIL)
                                .path(path)
                                .album(album)
                                .build()
                );
                album.getImages().add(img);
            }
            else {
                exceptions.add("Image " + image.getOriginalFilename() + " is corrupted " +
                        "or doesn't have png, jpg, jpeg, webp or jpeg2000 extension.");
            }
        }
        return new ResponsePair<>(album.toDTO(), exceptions);
    }

    @Override
    @Transactional
    public void remove(long id) throws AlbumException, IOException {
        Album album = albumRepository.findById(id).orElseThrow(() -> new AlbumException("Album with id " + id + " doesn't exist"));
        FileSaver.delete(album.getCoverImage());
        for (Image image : album.getImages()) {
            FileSaver.delete(image.getPath());
        }
        albumRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ResponsePair<AlbumDTO> update(long id, MultipartFile coverImage, MultipartFile[] image, List<Long> deletedImages, String isPublic) throws AlbumException, IOException {
        List<String> exceptions = new ArrayList<>();
        Album album = albumRepository.findById(id).orElseThrow(() -> new AlbumException("Album with id " + id + " doesn't exist"));
        if (isPublic != null)
            album.setPublic(isPublic.equals("true"));
        String imagePath = null;
        if (coverImage != null) {
            imagePath = FileSaver.save(coverImage, "covers", coverImage.getOriginalFilename());
            if (imagePath != null && (imagePath.endsWith(".png") || imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg")))
                FileSaver.delete(album.getCoverImage());
        }
        if (imagePath != null) {
            album.setCoverImage(imagePath);
        }
        if (deletedImages != null) {
            for (long imageId : deletedImages) {
                Image imageToDelete = imageRepository.findById(imageId).orElseThrow(() -> new AlbumException("Image with id " + imageId + " doesn't exist"));
                String filename = imageToDelete.getFilename().substring(imageToDelete.getKey().getKey().length());
                List<Image> imagesToDelete = album.getImages().stream()
                        .filter(img -> img.getFilename().endsWith(filename))
                        .collect(Collectors.toList());
                for (Image img : imagesToDelete) {
                    FileSaver.delete(img.getPath());
                    album.getImages().remove(img);
                    imageRepository.deleteById(img.getId());
                }
            }
        }

        if (image != null) {
            for (MultipartFile file : image) {
                if (file != null && file.getOriginalFilename() != null && file.getOriginalFilename().matches(".+\\.(png|jpg|jpeg|webp|jpeg2000)")) {
                    String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                    String path = FileSaver.save(file, "images", ImageKey.ORIGINAL.getKey() + id + extension);
                    String originalFilename = path != null ? path.substring(path.lastIndexOf("/") + 1) : null;
                    Image img = imageRepository.save(
                            Image.builder()
                                    .filename(originalFilename)
                                    .key(ImageKey.ORIGINAL)
                                    .path(path)
                                    .album(album)
                                    .build()
                    );
                    album.getImages().add(img);
                    path = FileSaver.saveResized("images", originalFilename, ImageKey.THUMBNAIL.getKey() + id + extension);
                    originalFilename = path != null ? path.substring(path.lastIndexOf("/") + 1) : null;
                    img = imageRepository.save(
                            Image.builder()
                                    .filename(originalFilename)
                                    .key(ImageKey.THUMBNAIL)
                                    .path(path)
                                    .album(album)
                                    .build()
                    );
                    album.getImages().add(img);
                }
                else {
                    exceptions.add("Image " + file.getOriginalFilename() + " is corrupted " +
                            "or doesn't have png, jpg, jpeg, webp or jpeg2000 extension.");
                }
            }
        }
        return new ResponsePair<>(album.toDTO(), exceptions);
    }
}
