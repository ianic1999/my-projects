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
    private static final String RESET_LINK = "https://fablab.md/auth/reset";
    private static String ADMIN_EMAIL = "fablab.chisinau@gmail.com";
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

    @Override
    public void sendMessageToAdmin(String fullName, String email, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(ADMIN_EMAIL);
        mailMessage.setFrom(SERVER_EMAIL);
        mailMessage.setSubject("Message from FABLAB website");
        mailMessage.setText("Full Name: " + fullName + "\nEmail: " + email  + "\nMessage: " + message);
        mailSender.send(mailMessage);
    }

    @Override
    public void sendInvitation(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(SERVER_EMAIL);
        message.setSubject("FABLAB Invitation");
        message.setText("Welcome to FABLAB team. Please click the following link to set your password: " +
                RESET_LINK + "?token=" + token);
        mailSender.send(message);
    }

    @Override
    public void sendSpaceBooking(String fullName, String email, String telephoneNumber, String dateTime, String description, String spaceName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(ADMIN_EMAIL);
        message.setFrom(SERVER_EMAIL);
        message.setSubject("Space Booking");
        message.setText("Full Name: " + fullName + "\nEmail: " +
                email + "\nTelephone Number: " + telephoneNumber + "\nDate and Time: " + dateTime +
                "\nDescription: " + description + "\nSpace Name: " + spaceName);
        mailSender.send(message);
    }
}
