package com.example.demo.service;

import com.example.demo.dto.AgentDTO;
import com.example.demo.model.exception.AgentException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AgentService {
    Page<AgentDTO> get(int page, int perPage);
    AgentDTO getById(long id) throws AgentException;
    AgentDTO add(String firstName,
                 String lastName,
                 String phone,
                 String email,
                 MultipartFile image) throws IOException;
    AgentDTO update(long id,
                    String firstName,
                    String lastName,
                    String phone,
                    String email,
                    MultipartFile image) throws AgentException, IOException;
    void remove(long id) throws AgentException, IOException;
}
