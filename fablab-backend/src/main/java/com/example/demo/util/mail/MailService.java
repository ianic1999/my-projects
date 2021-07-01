package com.example.demo.util.mail;

import com.example.demo.dto.email.SpaceBookingRequest;

public interface MailService {
    void sendResetLink(String email, String token);
    void sendMessageToAdmin(String fullName, String email, String message);
    void sendInvitation(String email, String token);
    void sendSpaceBooking(String fullName, String email, String telephoneNumber, String dateTime, String description, String spaceName);
}
