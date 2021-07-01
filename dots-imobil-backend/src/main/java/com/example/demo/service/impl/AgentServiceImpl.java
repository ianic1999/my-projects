package com.example.demo.service.impl;

import com.example.demo.dto.AgentDTO;
import com.example.demo.model.Agent;
import com.example.demo.model.exception.AgentException;
import com.example.demo.repository.AgentRepository;
import com.example.demo.service.AgentService;
import com.example.demo.util.FileSaver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {
    private final AgentRepository agentRepository;

    @Override
    public Page<AgentDTO> get(int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return agentRepository.findAll(pageable).map(Agent::toDTO);
    }

    @Override
    public AgentDTO getById(long id) throws AgentException {
        return agentRepository.findById(id).orElseThrow(() -> new AgentException("Agent with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    public AgentDTO add(String firstName, String lastName, String phone, String email, MultipartFile image) throws IOException {
        String imagePath = FileSaver.save(image, "agents", image.getOriginalFilename());
        Agent agent = Agent.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .image(imagePath)
                .build();
        return agentRepository.save(agent).toDTO();
    }

    @Override
    @Transactional
    public AgentDTO update(long id, String firstName, String lastName, String phone, String email, MultipartFile image) throws AgentException, IOException {
        Agent agent = agentRepository.findById(id).orElseThrow(() -> new AgentException("Agent with id " + id + " doesn't exist"));
        String imagePath = null;
        if (image != null) {
            FileSaver.delete(agent.getImage());
            imagePath = FileSaver.save(image, "agents", image.getOriginalFilename());
        }
        if (firstName != null) {
            agent.setFirstName(firstName);
        }
        if (lastName != null) {
            agent.setLastName(lastName);
        }
        if (phone != null) {
            agent.setPhone(phone);
        }
        if (email != null) {
            agent.setEmail(email);
        }
        if (imagePath != null) {
            agent.setImage(imagePath);
        }
        return agent.toDTO();
    }

    @Override
    public void remove(long id) throws AgentException, IOException {
        Agent agent = agentRepository.findById(id).orElseThrow(() -> new AgentException("Agent with id " + id + " doesn't exist"));
        FileSaver.delete(agent.getImage());
        agentRepository.deleteById(id);
    }
}
