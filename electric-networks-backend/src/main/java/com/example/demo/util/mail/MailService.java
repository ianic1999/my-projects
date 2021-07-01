package com.example.demo.util.mail;

public interface MailService {
    void sendResetLink(String email, String token);
}
