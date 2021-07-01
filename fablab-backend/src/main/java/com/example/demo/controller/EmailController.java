package com.example.demo.controller;

import com.example.demo.dto.email.EmailRequest;
import com.example.demo.dto.email.SpaceBookingRequest;
import com.example.demo.dto.response.Response;
import com.example.demo.util.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin
@RequiredArgsConstructor
public class EmailController {
    private final MailService mailService;

    @PostMapping
    public ResponseEntity<Response<String>> sendMailToAdmin(@RequestBody EmailRequest request) {
        mailService.sendMessageToAdmin(request.getFullName(), request.getEmail(), request.getMessage());
        return ResponseEntity.ok(
                new Response<>(request.getEmail())
        );
    }

    @PostMapping("/booking")
    public ResponseEntity<Response<String>> sendSpaceBooking(@RequestBody SpaceBookingRequest request) {
        mailService.sendSpaceBooking(request.getFullName(), request.getEmail(), request.getTelephoneNumber(), request.getDateTime(), request.getDescription(), request.getSpaceName());
        return ResponseEntity.ok(
            new Response<>(request.getEmail())
        );
    }
}
