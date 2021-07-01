package com.example.demo.service.impl;

import com.example.demo.dto.EventDTO;
import com.example.demo.model.Event;
import com.example.demo.model.User;
import com.example.demo.model.exception.EventException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EventService;
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
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public Page<EventDTO> getAll(int page, int size, String order, boolean ascending, String search, String upcoming) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, size)
                : ascending ? PageRequest.of(page - 1, size, Sort.by(order).ascending())
                    : PageRequest.of(page - 1, size, Sort.by(order).descending());
        if (upcoming == null)
            return eventRepository.findByTitleContainsIgnoreCase(search, pageable).map(Event::toDTO);
        else {
            boolean isUpcoming = upcoming.equals("true");
            if (isUpcoming)
                return eventRepository.findByTitleContainsIgnoreCaseAndDueDateAtAfter(search, LocalDateTime.now(), pageable).map(Event::toDTO);
            else
                return eventRepository.findByTitleContainsIgnoreCaseAndDueDateAtBefore(search, LocalDateTime.now(), pageable).map(Event::toDTO);
        }

    }

    @Override
    public EventDTO getById(long id) throws EventException {
        return eventRepository.findById(id).orElseThrow(() -> new EventException("Event with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    public EventDTO add(String title, String body, String link, String location, LocalDateTime dueDateAt, MultipartFile image, long userId) throws IOException, UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User with id " + userId + " doesn't exist"));
        String imagePath = FileSaver.save(image, "events", image.getOriginalFilename());
        Event event = Event.builder()
                .title(title)
                .body(body)
                .location(location)
                .dueDateAt(dueDateAt)
                .link(link)
                .image(imagePath)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(user)
                .build();
        return eventRepository.save(event).toDTO();
    }

    @Override
    @Transactional
    public EventDTO update(long id, String title, String body, String link, String location, LocalDateTime dueDateAt, MultipartFile image) throws EventException, IOException {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EventException("Event with id " + id + " doesn't exist"));
        String imagePath = null;
        if (image != null) {
            imagePath = FileSaver.save(image, "events", image.getOriginalFilename());
            if (imagePath != null && (imagePath.endsWith(".png") || imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg")))
                FileSaver.delete(event.getImage());
        }
        if (title != null)
            event.setTitle(title);;
        if (body != null)
            event.setBody(body);
        if (location != null)
            event.setLocation(location);
        if (dueDateAt != null)
            event.setDueDateAt(dueDateAt);
        if (link != null)
            event.setLink(link);
        if (imagePath != null)
            event.setImage(imagePath);
        event.setUpdatedAt(LocalDateTime.now());
        return event.toDTO();
    }

    @Override
    public void remove(long id) {
        eventRepository.deleteById(id);
    }
}
