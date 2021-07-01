package com.example.demo.service.impl;

import com.example.demo.dto.PartnerDTO;
import com.example.demo.model.Partner;
import com.example.demo.model.User;
import com.example.demo.model.exception.PartnerException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.PartnerRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PartnerService;
import com.example.demo.util.FileSaver;
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
public class PartnerServiceImpl implements PartnerService {
    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;

    @Override
    public Page<PartnerDTO> getAll(int page, int size, String order, boolean ascending, String search) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, size)
                : ascending ? PageRequest.of(page - 1, size, Sort.by(order).ascending())
                    : PageRequest.of(page - 1, size, Sort.by(order).descending());
        return partnerRepository.findByNameContainsIgnoreCase(search, pageable).map(Partner::toDTO);
    }

    @Override
    public PartnerDTO getById(long id) throws PartnerException {
        return partnerRepository.findById(id).orElseThrow(() -> new PartnerException("Partner with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    public PartnerDTO add(String name, MultipartFile image, String link, long userId) throws IOException, UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User with id " + userId + " doesn't exist"));
        String imagePath = FileSaver.save(image, "partners", image.getOriginalFilename());
        Partner partner = Partner.builder()
                .name(name)
                .image(imagePath)
                .link(link)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(user)
                .build();
        return partnerRepository.save(partner).toDTO();
    }

    @Override
    @Transactional
    public PartnerDTO update(long id, String name, MultipartFile image, String link) throws PartnerException, IOException {
        Partner partner = partnerRepository.findById(id).orElseThrow(() -> new PartnerException("Partner with id " + id + " doesn't exist"));
        String imagePath = null;
        if (image != null) {
            imagePath = FileSaver.save(image, "partners", image.getOriginalFilename());
        }
        if (imagePath != null && (imagePath.endsWith(".png") || imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg")))
            FileSaver.delete(partner.getImage());
        if (name != null)
            partner.setName(name);
        if (imagePath != null)
            partner.setImage(imagePath);
        if (name != null)
            partner.setLink(link);
        partner.setUpdatedAt(LocalDateTime.now());
        return partner.toDTO();
    }

    @Override
    public void remove(long id) throws PartnerException, IOException {
        Partner partner = partnerRepository.findById(id).orElseThrow(() -> new PartnerException("Partner with id " + id + " doesn't exist"));
        FileSaver.delete(partner.getImage());
        partnerRepository.deleteById(id);
    }
}
