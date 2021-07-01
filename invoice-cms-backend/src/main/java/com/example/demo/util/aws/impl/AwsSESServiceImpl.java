package com.example.demo.util.aws.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.example.demo.dto.CompanyDTO;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.request.ContactRequest;
import com.example.demo.model.Company;
import com.example.demo.model.Customer;
import com.example.demo.model.Invoice;
import com.example.demo.model.enums.UserType;
import com.example.demo.util.aws.AwsSESService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class AwsSESServiceImpl implements AwsSESService {
    private final AmazonSimpleEmailService emailService;
    private static final String CHAR_SET = "UTF-8";
    private static final int REQUEST_TIMEOUT = 3000;

    @Value("${aws.ses.sender}")
    private String sender;

    @Value("${aws.ses.support-email}")
    private String supportEmail;

    private static final String RESET_LINK = "https://portal.oklyx.com/auth/reset";

    @Override
    public  void sendResetLink(String email, String lastName, String token) {
        String link = RESET_LINK + "?token=" + token;
        String htmlBody = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <title>Reset Passowrd</title>\n" +
                "  <link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.15.3/css/all.css\"\n" +
                "        integrity=\"sha384-SZXxX4whJ79/gErwcOYf+zWLeJdY/qpuqC4cAa9rOGUstPomtqpuNWT9wdPEn2fk\" crossorigin=\"anonymous\">\n" +
                "  <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\">\n" +
                "  <link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap\" rel=\"stylesheet\">\n" +
                "  <style>\n" +
                "      body {\n" +
                "          width: 100%;\n" +
                "          background-color: #ffffff;\n" +
                "          margin: 0;\n" +
                "          padding: 0;\n" +
                "          height: 100vh;\n" +
                "          display: flex; /* WIDTH and HEIGHT are required*/\n" +
                "          justify-content: center;\n" +
                "          align-items: center;\n" +
                "      }\n" +
                "\n" +
                "      table {\n" +
                "          border-collapse: collapse;\n" +
                "      }\n" +
                "\n" +
                "      img {\n" +
                "          border: none;\n" +
                "          display: block;\n" +
                "      }\n" +
                "\n" +
                "      .border-lr {\n" +
                "          border-left: 1px solid #dadada;\n" +
                "          border-right: 1px solid #dadada;\n" +
                "      }\n" +
                "\n" +
                "      h2#name {\n" +
                "          background: white;\n" +
                "          margin: 0 50px;\n" +
                "          padding: 20px;\n" +
                "          font-weight: 700;\n" +
                "          font-family: 'Inter', sans-serif;\n" +
                "      }\n" +
                "\n" +
                "      .text {\n" +
                "          margin: 0 50px;\n" +
                "          padding: 0 20px 20px;\n" +
                "          background: white;\n" +
                "          font-weight: 400;\n" +
                "          font-family: 'Inter', sans-serif;\n" +
                "      }\n" +
                "\n" +
                "      .p-gray-background {\n" +
                "          margin: 0;\n" +
                "          background: #eee;\n" +
                "          padding: 5px;\n" +
                "      }\n" +
                "\n" +
                "      .p-link {\n" +
                "          color: #11a1fd;\n" +
                "          text-decoration: unset;\n" +
                "      }\n" +
                "\n" +
                "      a.button {\n" +
                "          background: #11a1fd;\n" +
                "          color: white;\n" +
                "          text-decoration: unset;\n" +
                "          padding: 9.4px 1rem;\n" +
                "          border-radius: 4px;\n" +
                "          font-weight: 600;\n" +
                "          font-family: 'Inter', sans-serif;\n" +
                "      }\n" +
                "\n" +
                "      a.button:hover {\n" +
                "          background: rgba(17, 161, 253, 0.6);\n" +
                "      }\n" +
                "\n" +
                "      a i {\n" +
                "          color: gray;\n" +
                "          font-size: 30px;\n" +
                "          padding: 0 10px;\n" +
                "      }\n" +
                "\n" +
                "\n" +
                "      @media only screen and (max-width: 640px) {\n" +
                "          body .deviceWidth {\n" +
                "              width: 440px !important;\n" +
                "              padding: 0;\n" +
                "          }\n" +
                "      }\n" +
                "\n" +
                "      @media only screen and (max-width: 479px) {\n" +
                "          body .deviceWidth {\n" +
                "              width: 280px !important;\n" +
                "              padding: 0;\n" +
                "          }\n" +
                "      }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<!-- Wrapper -->\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\">\n" +
                "\n" +
                "  <tr>\n" +
                "    <td>\n" +
                "      <!-- Logo -->\n" +
                "      <table width=\"100%\" class=\"border-lr deviceWidth\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\"\n" +
                "             bgcolor=\"#eeeeed\">\n" +
                "        <tr>\n" +
                "          <td style=\"display: flex;align-items: center; justify-content: center; padding: 20px\">\n" +
                "            <a href=\"#\"><img class=\"deviceWidth\" width=\"50\" height=\"50\"\n" +
                "                             src=\"https://portal.oklyx.com/assets/icons/logo.svg\"\n" +
                "                             alt=\"\" border=\"0\" /></a>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "\n" +
                "      </table>\n" +
                "      <!-- End Logo -->\n" +
                "\n" +
                "      <!--Text -->\n" +
                "      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" class=\"border-lr deviceWidth\"\n" +
                "             bgcolor=\"#eeeeed\">\n" +
                "        <tr>\n" +
                "          <td align=\"center\">\n" +
                "            <h2 id=\"name\">\n" +
                "              Hey, " + lastName + "!\n" +
                "            </h2>\n" +
                "\n" +
                "            <p class=\"text\">\n" +
                "              You recently requested to reset your password for your account. Use this button below to reset it.\n" +
                "              <b> This password reset is only valid for the next 24 hours.</b>\n" +
                "            </p>\n" +
                "\n" +
                "            <p class=\"text\" style=\"text-align: center\">\n" +
                "              <a href=\"" + link + "\" class=\"button\">\n" +
                "                Reset your password\n" +
                "              </a>\n" +
                "            </p>\n" +
                "\n" +
                "            <div class=\"text\" style=\"padding: 20px;\">\n" +
                "              <p class=\"p-gray-background\">\n" +
                "                If you have any questions, feel free to <a class=\"p-link\" href=\"https://oklyx.com/contact\"\n" +
                "                                                           target=\"_blank\"> email our customer success team</a>\n" +
                "              </p>\n" +
                "            </div>\n" +
                "\n" +
                "            <p class=\"text\">\n" +
                "              Thanks,\n" +
                "              <br />\n" +
                "              Oklyx Team\n" +
                "            </p>\n" +
                "\n" +
                "            <p class=\"text\">\n" +
                "              If you're having trouble with the button above, just click to link below.\n" +
                "            </p>\n" +
                "\n" +
                "            <p class=\"text\">\n" +
                "              <a class=\"p-link\" href=\"" + link + "\" target=\"_blank\">Click here to reset password</a>\n" +
                "            </p>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "      <!-- End Text -->\n" +
                "\n" +
                "\n" +
                "      <!-- Footer -->\n" +
                "      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" class=\"deviceWidth\"\n" +
                "             bgcolor=\"#eeeeed\">\n" +
                "        <tr>\n" +
                "          <td align=\"center\" valign=\"top\" id=\"socials\" style=\"padding: 20px\">\n" +
                "            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "              <tr>\n" +
                "                <td>\n" +
                "                  <a href=\"#\"><i class=\"fab fa-facebook\"></i></a>\n" +
                "                </td>\n" +
                "                <td>\n" +
                "                  <a href=\"#\"><i class=\"fab fa-twitter-square\"></i></a>\n" +
                "                </td>\n" +
                "                <td>\n" +
                "                  <a href=\"#\"><i class=\"fab fa-snapchat-square\"></i></a>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "      <!-- End of Footer-->\n" +
                "    </td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "<!-- End Wrapper -->\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withData(htmlBody)))
                        .withSubject(new Content()
                                .withCharset(CHAR_SET).withData("Complete Password Reset!")))
                .withSource(sender).withSdkRequestTimeout(REQUEST_TIMEOUT);
        emailService.sendEmail(request);
    }

    @Override
    public void sendInvoice(Invoice invoice, String filename) throws MessagingException, IOException {
        Session session = Session.getDefaultInstance(new Properties());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        CustomerDTO customer = mapper.readValue(invoice.getCustomer(), CustomerDTO.class);
        CompanyDTO company = invoice.getCompany() != null ? mapper.readValue(invoice.getCompany(), CompanyDTO.class) : null;
        String body = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <title>Invoice</title>\n" +
                "  <link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.15.3/css/all.css\"\n" +
                "        integrity=\"sha384-SZXxX4whJ79/gErwcOYf+zWLeJdY/qpuqC4cAa9rOGUstPomtqpuNWT9wdPEn2fk\" crossorigin=\"anonymous\">\n" +
                "  <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\">\n" +
                "  <link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap\" rel=\"stylesheet\">\n" +
                "  <style>\n" +
                "      body {\n" +
                "          width: 100%;\n" +
                "          background-color: #ffffff;\n" +
                "          margin: 0;\n" +
                "          padding: 0;\n" +
                "          height: 100vh;\n" +
                "          display: flex; /* WIDTH and HEIGHT are required*/\n" +
                "          justify-content: center;\n" +
                "          align-items: center;\n" +
                "      }\n" +
                "\n" +
                "      table {\n" +
                "          border-collapse: collapse;\n" +
                "      }\n" +
                "\n" +
                "      img {\n" +
                "          border: none;\n" +
                "          display: block;\n" +
                "      }\n" +
                "\n" +
                "      .border-lr {\n" +
                "          border-left: 1px solid #dadada;\n" +
                "          border-right: 1px solid #dadada;\n" +
                "      }\n" +
                "\n" +
                "      h2#name {\n" +
                "          background: white;\n" +
                "          margin: 0 50px;\n" +
                "          padding: 20px;\n" +
                "          font-weight: 700;\n" +
                "          font-family: 'Inter', sans-serif;\n" +
                "      }\n" +
                "\n" +
                "      .text {\n" +
                "          margin: 0 50px;\n" +
                "          padding: 0 20px 20px;\n" +
                "          background: white;\n" +
                "          font-weight: 400;\n" +
                "          font-family: 'Inter', sans-serif;\n" +
                "      }\n" +
                "\n" +
                "      a i {\n" +
                "          color: gray;\n" +
                "          font-size: 30px;\n" +
                "          padding: 0 10px;\n" +
                "      }\n" +
                "\n" +
                "\n" +
                "      @media only screen and (max-width: 640px) {\n" +
                "          body .deviceWidth {\n" +
                "              width: 440px !important;\n" +
                "              padding: 0;\n" +
                "          }\n" +
                "      }\n" +
                "\n" +
                "      @media only screen and (max-width: 479px) {\n" +
                "          body .deviceWidth {\n" +
                "              width: 280px !important;\n" +
                "              padding: 0;\n" +
                "          }\n" +
                "      }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<!-- Wrapper -->\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\">\n" +
                "\n" +
                "  <tr>\n" +
                "    <td>\n" +
                "      <!-- Logo -->\n" +
                "      <table width=\"600\" class=\"border-lr deviceWidth\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\"\n" +
                "             bgcolor=\"#eeeeed\">\n" +
                "        <tr>\n" +
                "          <td style=\"padding: 20px;\" align=\"center\">\n" +
                "            <a href=\"#\"><img class=\"deviceWidth\" height=\"50\"\n" +
                "                             src=\"https://obmcpq.stripocdn.email/content/guids/CABINET_12f70f1d232585c2535bcf57a2dfd984/images/54851623239900217.png\"\n" +
                "                             alt=\"\" border=\"0\" /></a>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "\n" +
                "      </table>\n" +
                "      <!-- End Logo -->\n" +
                "\n" +
                "      <!--Text -->\n" +
                "      <table width=\"600\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" class=\"border-lr deviceWidth\"\n" +
                "             bgcolor=\"#eeeeed\">\n" +
                "        <tr>\n" +
                "          <td>\n" +
                "            <h2 id=\"name\">\n" +
                "              Hey, " + customer.getFirstName() + "!\n" +
                "            </h2>\n" +
                "\n" +
                "            <p class=\"text\">\n" +
                "              " + invoice.getCreatedBy().getFirstName() + " " + invoice.getCreatedBy().getLastName() + (invoice.getCreatedBy().getType() != null && invoice.getCreatedBy().getType().equals(UserType.ENTREPRENEUR) ? " from " + company.getName() : "") + " just sent you an invoice in amount of " + invoice.getCurrency().getKey() + invoice.getTotal() + " for services of goods.\n" +
                "              The invoice is attached bellow.\n" +
                "            </p>\n" +
                "\n" +
                "            <p class=\"text\">\n" +
                "              Thanks,\n" +
                "              <br />\n" +
                "              King regards\n" +
                "            </p>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "      <!-- End Text -->\n" +
                "\n" +
                "\n" +
                "      <!-- Footer -->\n" +
                "      <table width=\"600\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" class=\"deviceWidth\"\n" +
                "             bgcolor=\"#eeeeed\">\n" +
                "        <tr>\n" +
                "          <td align=\"center\" valign=\"top\" id=\"socials\" style=\"padding: 20px\">\n" +
                "            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "              <tr>\n" +
                "                <td>\n" +
                "                  <a href=\"#\"><i class=\"fab fa-facebook\"></i></a>\n" +
                "                </td>\n" +
                "                <td>\n" +
                "                  <a href=\"#\"><i class=\"fab fa-twitter-square\"></i></a>\n" +
                "                </td>\n" +
                "                <td>\n" +
                "                  <a href=\"#\"><i class=\"fab fa-snapchat-square\"></i></a>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "      <!-- End of Footer-->\n" +
                "    </td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "<!-- End Wrapper -->\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";
        MimeMessage message = new MimeMessage(session);
        message.setSubject("New invoice was sent from " + invoice.getCreatedBy().getLastName() + (company != null ? " at " + company.getName() : ""), "UTF-8");
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(customer.getEmail()));
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(body, "text/html");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName("Invoice-" +invoice.getOrdinalNumber() + ".pdf");
        multipart.addBodyPart(messageBodyPart);
        message.setContent(multipart);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeTo(outputStream);
        RawMessage rawMessage =
                new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

        SendRawEmailRequest rawEmailRequest =
                new SendRawEmailRequest(rawMessage);

        emailService.sendRawEmail(rawEmailRequest);
    }

    @Override
    public void sendRegistrationCode(String email, String code) {
        String body = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <title>Verification Code</title>\n" +
                "  <link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.15.3/css/all.css\"\n" +
                "        integrity=\"sha384-SZXxX4whJ79/gErwcOYf+zWLeJdY/qpuqC4cAa9rOGUstPomtqpuNWT9wdPEn2fk\" crossorigin=\"anonymous\">\n" +
                "  <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\">\n" +
                "  <link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap\" rel=\"stylesheet\">\n" +
                "  <style>\n" +
                "      body {\n" +
                "          width: 100%;\n" +
                "          background-color: #ffffff;\n" +
                "          margin: 0;\n" +
                "          padding: 0;\n" +
                "          height: 100vh;\n" +
                "          display: flex; /* WIDTH and HEIGHT are required*/\n" +
                "          justify-content: center;\n" +
                "          align-items: center;\n" +
                "      }\n" +
                "\n" +
                "      table {\n" +
                "          border-collapse: collapse;\n" +
                "      }\n" +
                "\n" +
                "      img {\n" +
                "          border: none;\n" +
                "          display: block;\n" +
                "      }\n" +
                "\n" +
                "      .border-lr {\n" +
                "          border-left: 1px solid #dadada;\n" +
                "          border-right: 1px solid #dadada;\n" +
                "      }\n" +
                "\n" +
                "      h2#name {\n" +
                "          background: white;\n" +
                "          margin: 0 50px;\n" +
                "          padding: 20px;\n" +
                "          font-weight: 700;\n" +
                "          font-family: 'Inter', sans-serif;\n" +
                "      }\n" +
                "\n" +
                "      .text {\n" +
                "          margin: 0 50px;\n" +
                "          padding: 0 20px 20px;\n" +
                "          background: white;\n" +
                "          font-weight: 400;\n" +
                "          font-family: 'Inter', sans-serif;\n" +
                "      }\n" +
                "\n" +
                "      .code {\n" +
                "          background: #eee;\n" +
                "          padding: 2px;\n" +
                "      }\n" +
                "\n" +
                "      a i {\n" +
                "          color: gray;\n" +
                "          font-size: 30px;\n" +
                "          padding: 0 10px;\n" +
                "      }\n" +
                "\n" +
                "\n" +
                "      @media only screen and (max-width: 640px) {\n" +
                "          body .deviceWidth {\n" +
                "              width: 440px !important;\n" +
                "              padding: 0;\n" +
                "          }\n" +
                "      }\n" +
                "\n" +
                "      @media only screen and (max-width: 479px) {\n" +
                "          body .deviceWidth {\n" +
                "              width: 280px !important;\n" +
                "              padding: 0;\n" +
                "          }\n" +
                "      }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<!-- Wrapper -->\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\">\n" +
                "\n" +
                "  <tr>\n" +
                "    <td>\n" +
                "      <!-- Logo -->\n" +
                "      <table width=\"600\" class=\"border-lr deviceWidth\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\"\n" +
                "             bgcolor=\"#eeeeed\">\n" +
                "        <tr>\n" +
                "          <td style=\"padding: 20px;\" align=\"center\">\n" +
                "            <a href=\"#\"><img class=\"deviceWidth\" height=\"50\"\n" +
                "                             src=\"https://obmcpq.stripocdn.email/content/guids/CABINET_12f70f1d232585c2535bcf57a2dfd984/images/54851623239900217.png\"\n" +
                "                             alt=\"\" border=\"0\" /></a>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "\n" +
                "      </table>\n" +
                "      <!-- End Logo -->\n" +
                "\n" +
                "      <!--Text -->\n" +
                "      <table width=\"600\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" class=\"border-lr deviceWidth\"\n" +
                "             bgcolor=\"#eeeeed\">\n" +
                "        <tr>\n" +
                "          <td>\n" +
                "            <h2 id=\"name\">\n" +
                "              Welcome!\n" +
                "            </h2>\n" +
                "\n" +
                "            <p class=\"text\">\n" +
                "              <b> We’re so happy to have you on board.</b>\n" +
                "              <br />\n" +
                "              Use the following <span class=\"code\">" + code + "</span> code to activate your account\n" +
                "            </p>\n" +
                "\n" +
                "            <p class=\"text\">\n" +
                "              Thanks,\n" +
                "              <br />\n" +
                "              King regards\n" +
                "            </p>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "      <!-- End Text -->\n" +
                "\n" +
                "\n" +
                "      <!-- Footer -->\n" +
                "      <table width=\"600\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" class=\"deviceWidth\"\n" +
                "             bgcolor=\"#eeeeed\">\n" +
                "        <tr>\n" +
                "          <td align=\"center\" valign=\"top\" id=\"socials\" style=\"padding: 20px\">\n" +
                "            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "              <tr>\n" +
                "                <td>\n" +
                "                  <a href=\"#\"><i class=\"fab fa-facebook\"></i></a>\n" +
                "                </td>\n" +
                "                <td>\n" +
                "                  <a href=\"#\"><i class=\"fab fa-twitter-square\"></i></a>\n" +
                "                </td>\n" +
                "                <td>\n" +
                "                  <a href=\"#\"><i class=\"fab fa-snapchat-square\"></i></a>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "      <!-- End of Footer-->\n" +
                "    </td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "<!-- End Wrapper -->\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset(CHAR_SET).withData(body)))
                        .withSubject(new Content()
                                .withCharset(CHAR_SET).withData("Complete your registration!")))
                .withSource(sender).withSdkRequestTimeout(REQUEST_TIMEOUT);
        emailService.sendEmail(request);
    }

    @Override
    public void sendContactUsEmail(ContactRequest request) {
        String body = "Name: " + request.getName() + "\n"
                + "Email: " + request.getEmail() + "\n"
                + "Topic: " + request.getTopic() + "\n"
                + "Message: " + request.getMessage();
        SendEmailRequest sendEmailRequest = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(supportEmail))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withText(new Content()
                                        .withCharset(CHAR_SET).withData(body)))
                        .withSubject(new Content()
                                .withCharset(CHAR_SET).withData("You have new message!")))
                .withSource(sender).withSdkRequestTimeout(REQUEST_TIMEOUT);
        emailService.sendEmail(sendEmailRequest);
    }
}
