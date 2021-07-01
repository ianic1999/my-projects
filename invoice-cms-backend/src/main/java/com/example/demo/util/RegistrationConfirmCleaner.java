package com.example.demo.util;

import com.example.demo.model.RegistrationConfirm;
import com.example.demo.repository.RegistrationConfirmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RegistrationConfirmCleaner {
    private final RegistrationConfirmRepository registrationConfirmRepository;

    @Scheduled(cron = "0 * * ? * *")
    public void removeExpiredCodes() {
        List<RegistrationConfirm> confirms = registrationConfirmRepository.findAll();
        confirms.forEach(confirm -> {
                    if (confirm.getDeadline().isBefore(LocalDateTime.now()))
                        registrationConfirmRepository.deleteById(confirm.getId());
                });
    }
}
