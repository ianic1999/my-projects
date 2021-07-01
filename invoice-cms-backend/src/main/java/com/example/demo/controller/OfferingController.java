package com.example.demo.controller;

import com.example.demo.dto.OfferingDTO;
import com.example.demo.dto.request.OfferingRequest;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.InvoiceException;
import com.example.demo.model.exception.OfferingException;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.OfferingService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offerings")
@CrossOrigin("*")
@RequiredArgsConstructor
public class OfferingController {
    private final OfferingService offeringService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<OfferingDTO>> getAll(@RequestParam(defaultValue ="1") int page,
                                                                 @RequestParam(defaultValue = "15") int perPage,
                                                                 @RequestParam(defaultValue = "") String search) throws UserException {
        return ResponseEntity.ok(
                new PaginatedResponse<>(offeringService.get(page, perPage, userService.getCurrentUser(), search))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<OfferingDTO>> getById(@PathVariable long id) throws OfferingException, UserException {
        return ResponseEntity.ok(
                new Response<>(offeringService.getById(id, userService.getCurrentUser()))
        );
    }

    @PostMapping
    public ResponseEntity<Response<OfferingDTO>> add(@RequestBody OfferingRequest offering) throws OfferingException, InvoiceException, UserException {
        return new ResponseEntity<>(
                new Response<>(offeringService.add(offering, userService.getCurrentUser())),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<OfferingDTO>> update(@PathVariable long id,
                                                        @RequestBody OfferingRequest offering) throws OfferingException, InvoiceException, UserException {
        offering.setId(id);
        return ResponseEntity.ok(
                new Response<>(offeringService.update(offering, userService.getCurrentUser()))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws UserException, OfferingException {
        offeringService.remove(id, userService.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}
