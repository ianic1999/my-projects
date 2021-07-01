package com.example.demo.util.aws;

import com.example.demo.dto.request.ContactRequest;
import com.example.demo.model.Invoice;

import javax.mail.MessagingException;
import java.io.IOException;

public interface AwsSESService {
    void sendResetLink(String email, String lastName, String token);
    void sendInvoice(Invoice invoice, String filename) throws MessagingException, IOException;
    void sendRegistrationCode(String email, String code);
    void sendContactUsEmail(ContactRequest request);
}
