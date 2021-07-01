package com.example.demo.controller;

import com.example.demo.dto.AgentDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.AgentException;
import com.example.demo.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AgentController {
    private final AgentService agentService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<AgentDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "15") int perPage) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(agentService.get(page, perPage))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<AgentDTO>> getById(@PathVariable long id) throws AgentException {
        return ResponseEntity.ok(
                new Response<>(agentService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<AgentDTO>> add(@RequestParam String firstName,
                                                  @RequestParam String lastName,
                                                  @RequestParam String phone,
                                                  @RequestParam String email,
                                                  @RequestParam MultipartFile image) throws IOException {
        return new ResponseEntity<>(
                new Response<>(agentService.add(firstName, lastName, phone, email, image)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{agentId}")
    public ResponseEntity<Response<AgentDTO>> updatePut(@PathVariable long agentId,
                                                        @RequestParam long id,
                                                        @RequestParam String firstName,
                                                        @RequestParam String lastName,
                                                        @RequestParam String phone,
                                                        @RequestParam String email,
                                                        @RequestParam MultipartFile image) throws AgentException, IOException {
        return ResponseEntity.ok(
                new Response<>(agentService.update(agentId, firstName, lastName, phone, email, image))
        );
    }

    @PatchMapping("/{agentId}")
    public ResponseEntity<Response<AgentDTO>> updatePatch(@PathVariable long agentId,
                                                          @RequestParam long id,
                                                          @RequestParam(required = false) String firstName,
                                                          @RequestParam(required = false) String lastName,
                                                          @RequestParam(required = false) String phone,
                                                          @RequestParam(required = false) String email,
                                                          @RequestParam(required = false) MultipartFile image) throws AgentException, IOException {
        return ResponseEntity.ok(
                new Response<>(agentService.update(agentId, firstName, lastName, phone, email, image))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws AgentException, IOException {
        agentService.remove(id);
        return ResponseEntity.noContent().build();
    }

}
