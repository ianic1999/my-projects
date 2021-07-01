package com.example.demo.controller;

import com.example.demo.dto.request.ContactRequest;
import com.example.demo.dto.response.Response;
import com.example.demo.util.aws.AwsSESService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ContactController {
    private final AwsSESService sesService;

    @PostMapping
    public ResponseEntity<Response<String>> sendContactEmail(@RequestBody ContactRequest request) {
        sesService.sendContactUsEmail(request);
        return ResponseEntity.ok(
                new Response<>("Your email was sent")
        );
    }
}
