package com.example.demo.controller;

import com.example.demo.dto.EventDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.EventException;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.EventService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/events")
@CrossOrigin("*")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<EventDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "15") int perPage,
                                                              @RequestParam(required = false) String order,
                                                              @RequestParam(defaultValue = "") String search,
                                                              @RequestParam(required = false) String upcoming) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(eventService.getAll(page, perPage, order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-"), search, upcoming))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<EventDTO>> getById(@PathVariable long id) throws EventException {
        return ResponseEntity.ok(
                new Response<>(eventService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<EventDTO>> add(@RequestParam String title,
                                                  @RequestParam String body,
                                                  @RequestParam String link,
                                                  @RequestParam String location,
                                                  @RequestParam String dueDateAt,
                                                  @RequestParam MultipartFile image) throws IOException, UserException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dueDateAt, formatter);
        return new ResponseEntity<>(
                new Response<>(eventService.add(title, body, link, location, dateTime, image, userService.getCurrentUser().getId())),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Response<EventDTO>> updatePut(@PathVariable long eventId,
                                                        @RequestParam String title,
                                                        @RequestParam String body,
                                                        @RequestParam String link,
                                                        @RequestParam String location,
                                                        @RequestParam String dueDateAt,
                                                        @RequestParam(required = false) MultipartFile image) throws EventException, IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dueDateAt, formatter);
        return ResponseEntity.ok(
                new Response<>(eventService.update(eventId, title, body, link, location, dateTime, image))
        );
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Response<EventDTO>> updatePatch(@PathVariable long eventId,
                                                          @RequestParam(required = false) String title,
                                                          @RequestParam(required = false) String body,
                                                          @RequestParam(required = false) String link,
                                                          @RequestParam(required = false) String location,
                                                          @RequestParam(required = false) String dueDateAt,
                                                          @RequestParam(required = false) MultipartFile image) throws EventException, IOException {
        LocalDateTime dateTime = null;
        if (dueDateAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            dateTime = LocalDateTime.parse(dueDateAt, formatter);
        }
        return ResponseEntity.ok(
                new Response<>(eventService.update(eventId, title, body, link, location, dateTime, image))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) {
        eventService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
