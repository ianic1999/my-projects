package com.example.demo.util.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private static final String RESET_LINK = "https://en.dots.md/auth/reset";
    @Value("${spring.mail.username}")
    private String SERVER_EMAIL;

    @Override
    public void sendResetLink(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Complete Password Reset!");
        message.setFrom(SERVER_EMAIL);
        message.setText("To complete the password reset process, please click here: " +
                RESET_LINK + "?token=" + token);
        mailSender.send(message);
    }
}
