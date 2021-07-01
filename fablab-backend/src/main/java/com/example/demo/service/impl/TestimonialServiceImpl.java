package com.example.demo.service.impl;

import com.example.demo.dto.TestimonialDTO;
import com.example.demo.model.Testimonial;
import com.example.demo.model.User;
import com.example.demo.model.exception.TestimonialException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.TestimonialRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TestimonialService;
import com.example.demo.util.FileSaver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class TestimonialServiceImpl implements TestimonialService {
    private final TestimonialRepository testimonialRepository;
    private final UserRepository userRepository;

    @Override
    public Page<TestimonialDTO> getAll(int page, int size, String order, boolean ascending, String search) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, size)
                : ascending ? PageRequest.of(page - 1, size, Sort.by(order).ascending())
                    : PageRequest.of(page - 1, size, Sort.by(order).descending());
        return testimonialRepository.findByNameContainsIgnoreCase(search, pageable).map(Testimonial::toDTO);
    }

    @Override
    public TestimonialDTO getById(long id) throws TestimonialException {
        return testimonialRepository.findById(id).orElseThrow(() -> new TestimonialException("Testimonial with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    public TestimonialDTO add(String name, String jobTitle, MultipartFile image, String message, long userId) throws IOException, UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User with id " + userId + " doesn't exist"));
        String imagePath = FileSaver.save(image, "testimonials", image.getOriginalFilename());
        Testimonial testimonial = Testimonial.builder()
                .name(name)
                .jobTitle(jobTitle)
                .image(imagePath)
                .message(message)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(user)
                .build();
        return testimonialRepository.save(testimonial).toDTO();
    }

    @Override
    @Transactional
    public TestimonialDTO update(long id, String name, String jobTitle, MultipartFile image, String message) throws TestimonialException, IOException {
        Testimonial testimonial = testimonialRepository.findById(id).orElseThrow(() -> new TestimonialException("Testimonial with id " + id + " doesn't exist"));
        String imagePath = null;
        if (image != null) {
            imagePath = FileSaver.save(image, "testimonials", image.getOriginalFilename());
        }
        if (imagePath != null && (imagePath.endsWith(".png") || imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg")))
            FileSaver.delete(testimonial.getImage());
        if (name != null)
            testimonial.setName(name);
        if (jobTitle != null)
            testimonial.setJobTitle(jobTitle);
        if (imagePath != null)
            testimonial.setImage(imagePath);
        if (message != null)
            testimonial.setMessage(message);
        testimonial.setUpdatedAt(LocalDateTime.now());
        return testimonial.toDTO();
    }

    @Override
    public void remove(long id) throws IOException, TestimonialException {
        Testimonial testimonial = testimonialRepository.findById(id).orElseThrow(() -> new TestimonialException("Testimonial with id " + id + " doesn't exist"));
        FileSaver.delete(testimonial.getImage());
        testimonialRepository.deleteById(id);
    }
}
