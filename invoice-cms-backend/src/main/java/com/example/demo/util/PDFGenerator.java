package com.example.demo.util;

import com.example.demo.dto.BankAccountDTO;
import com.example.demo.dto.CompanyDTO;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.Customer;
import com.example.demo.model.Invoice;
import com.example.demo.model.InvoiceOffering;
import com.example.demo.model.enums.Currency;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;

import java.io.*;

public class PDFGenerator {
    private static final String HEAD = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
            "    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\n" +
            "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
            "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
            "  <meta content=\"telephone=no\" name=\"format-detection\">\n" +
            "  <title>Invoice</title>\n" +
            "  <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\">\n" +
            "<link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap\" rel=\"stylesheet\">" +
            "  <!--[if (mso 16)]>\n" +
            "  <style type=\"text/css\">\n" +
            "    a {\n" +
            "      text-decoration: none;\n" +
            "    }\n" +
            "  </style>\n" +
            "  <![endif]-->\n" +
            "  <!--[if gte mso 9]>\n" +
            "  <style>sup {\n" +
            "    font-size: 100% !important;\n" +
            "  }</style><![endif]-->\n" +
            "  <!--[if gte mso 9]>\n" +
            "  <xml>\n" +
            "    <o:OfficeDocumentSettings>\n" +
            "      <o:AllowPNG></o:AllowPNG>\n" +
            "      <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
            "    </o:OfficeDocumentSettings>\n" +
            "  </xml>\n" +
            "  <![endif]-->\n" +
            "\n" +
            "  <style type=\"text/css\">\n" +
            "      #outlook a {\n" +
            "          padding: 0;\n" +
            "      }\n" +
            "\n" +
            "      .line {\n" +
            "          width: 100%;\n" +
            "          height: 2px;\n" +
            "          background: #e3eef6;\n" +
            "      }\n" +
            "\n" +
            "      .es-button {\n" +
            "          mso-style-priority: 100 !important;\n" +
            "          text-decoration: none !important;\n" +
            "      }\n" +
            "\n" +
            "      a[x-apple-data-detectors] {\n" +
            "          color: inherit !important;\n" +
            "          text-decoration: none !important;\n" +
            "          font-size: inherit !important;\n" +
            "          font-family: inherit !important;\n" +
            "          font-weight: inherit !important;\n" +
            "          line-height: inherit !important\n" +
            "      }\n" +
            "\n" +
            "      .es-desk-hidden {\n" +
            "          display: none;\n" +
            "          float: left;\n" +
            "          overflow: hidden;\n" +
            "          width: 0;\n" +
            "          max-height: 0;\n" +
            "          line-height: 0;\n" +
            "          mso-hide: all;\n" +
            "      }\n" +
            "\n" +
            "      [data-ogsb] .es-button {\n" +
            "\n" +
            "          border-width: 0 !important;\n" +
            "\n" +
            "          padding: 10px 20px !important;\n" +
            "      }\n" +
            "\n" +
            "      @media only screen and (max-width: 600px) {\n" +
            "          p, ul li, ol li, a {\n" +
            "              line-height: 150% !important\n" +
            "          }\n" +
            "\n" +
            "          h1 {\n" +
            "              font-size: 30px !important;\n" +
            "              text-align: center;\n" +
            "              line-height: 120%;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          h2 {\n" +
            "              font-size: 26px !important;\n" +
            "              text-align: center;\n" +
            "              line-height: 120%;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          h3 {\n" +
            "              font-size: 20px !important;\n" +
            "              text-align: center;\n" +
            "              line-height: 120%;\n" +
            "          }\n" +
            "\n" +
            "          .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a {\n" +
            "              font-size: 30px !important\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a {\n" +
            "              font-size: 26px !important;\n" +
            "          }\n" +
            "\n" +
            "          .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a {\n" +
            "              font-size: 20px !important;\n" +
            "          }\n" +
            "\n" +
            "          .es-menu td a {\n" +
            "              font-size: 16px !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a {\n" +
            "              font-size: 16px !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a {\n" +
            "              font-size: 16px !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a {\n" +
            "              font-size: 16px !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a {\n" +
            "              font-size: 12px !important;\n" +
            "          }\n" +
            "\n" +
            "          *[class=\"gmail-fix\"] {\n" +
            "              display: none !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 {\n" +
            "              text-align: center !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 {\n" +
            "              text-align: right !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 {\n" +
            "              text-align: left !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img {\n" +
            "              display: inline !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .es-button-border {\n" +
            "              display: block !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          a.es-button, button.es-button {\n" +
            "              font-size: 20px !important;\n" +
            "              display: block !important;\n" +
            "              border-width: 10px 0 !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .es-adaptive table, .es-left, .es-right {\n" +
            "              width: 100% !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header {\n" +
            "              width: 100% !important;\n" +
            "              max-width: 600px !important;\n" +
            "          }\n" +
            "\n" +
            "          .es-adapt-td {\n" +
            "              display: block !important;\n" +
            "              width: 100% !important;\n" +
            "          }\n" +
            "\n" +
            "\n" +
            "          .adapt-img {\n" +
            "              width: 100% !important;\n" +
            "              height: auto !important;\n" +
            "          }\n" +
            "\n" +
            "          .es-m-p0 {\n" +
            "              padding: 0 !important;\n" +
            "          }\n" +
            "\n" +
            "          .es-m-p0r {\n" +
            "              padding-right: 0 !important;\n" +
            "          }\n" +
            "\n" +
            "          .es-m-p0l {\n" +
            "              padding-left: 0 !important;\n" +
            "          }\n" +
            "\n" +
            "          .es-m-p0t {\n" +
            "              padding-top: 0 !important;\n" +
            "          }\n" +
            "\n" +
            "          .es-m-p0b {\n" +
            "              padding-bottom: 0 !important;\n" +
            "          }\n" +
            "\n" +
            "          .es-m-p20b {\n" +
            "              padding-bottom: 20px !important;\n" +
            "          }\n" +
            "\n" +
            "          .es-mobile-hidden, .es-hidden {\n" +
            "              display: none !important;\n" +
            "          }\n" +
            "\n" +
            "          tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden {\n" +
            "              width: auto !important;\n" +
            "              overflow: visible !important;\n" +
            "              float: none !important;\n" +
            "              max-height: inherit !important;\n" +
            "              line-height: inherit !important;\n" +
            "          }\n" +
            "\n" +
            "          tr.es-desk-hidden {\n" +
            "              display: table-row !important;\n" +
            "          }\n" +
            "\n" +
            "          table.es-desk-hidden {\n" +
            "              display: table !important;\n" +
            "          }\n" +
            "\n" +
            "          td.es-desk-menu-hidden {\n" +
            "              display: table-cell !important;\n" +
            "          }\n" +
            "\n" +
            "          .es-menu td {\n" +
            "              width: 1% !important;\n" +
            "          }\n" +
            "\n" +
            "          table.es-table-not-adapt, .esd-block-html table {\n" +
            "              width: auto !important;\n" +
            "          }\n" +
            "\n" +
            "          table.es-social {\n" +
            "              display: inline-block !important;\n" +
            "          }\n" +
            "\n" +
            "          table.es-social td {\n" +
            "              display: inline-block !important;\n" +
            "          }\n" +
            "      }\n" +
            "\n" +
            "\n" +
            "      .invoice-table .invoice-head td {\n" +
            "          color: #7d9eb5;\n" +
            "          font-size: 14px;\n" +
            "          border: none;\n" +
            "          line-height: 2px;\n" +
            "          border-bottom: 1px solid #e3eef6;\n" +
            "          height: 38px;\n" +
            "          font-family: 'Inter', sans-serif;\n" +
            "          font-weight: 600;\n" +
            "      }\n" +
            "\n" +
            "\n" +
            "      .invoice-table .invoice-body td {\n" +
            "          color: #223345;\n" +
            "          font-size: 14px;\n" +
            "          line-height: 1.62;\n" +
            "          border-bottom: 1px solid #e3eef6;\n" +
            "          height: 30px;\n" +
            "          font-family: 'Inter', sans-serif;\n" +
            "          font-weight: 400;\n" +
            "      }\n" +
            "\n" +
            "\n" +
            "      .title-offering {\n" +
            "          font-family: 'Inter', sans-serif;\n" +
            "          font-weight: 600;\n" +
            "          padding-bottom: 2.4px;\n" +
            "          font-size: 14px;\n" +
            "      }\n" +
            "\n" +
            "      .description-offering {\n" +
            "          font-size: 13px;\n" +
            "          overflow: hidden;\n" +
            "          text-overflow: ellipsis;\n" +
            "          display: -webkit-box;\n" +
            "          -webkit-box-orient: vertical;\n" +
            "          -webkit-line-clamp: 4;\n" +
            "          width: 95%;\n" +
            "      }\n" +
            "\n" +
            "      .logo {\n" +
            "          width: 50px;\n" +
            "          height: 50px;\n" +
            "      }\n" +
            "\n" +
            "      .logo img {\n" +
            "          width: 100%;\n" +
            "          height: 100%;\n" +
            "          object-fit: contain;\n" +
            "      }\n" +
            "  </style>\n" +
            "</head>";

    public static String generatePDF(Invoice invoice) throws IOException {
        String text = HEAD;
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        CustomerDTO customer = mapper.readValue(invoice.getCustomer(), CustomerDTO.class);
        CompanyDTO company = invoice.getCompany() != null ? mapper.readValue(invoice.getCompany(), CompanyDTO.class) : null;
        BankAccountDTO account = mapper.readValue(invoice.getBankAccount(), BankAccountDTO.class);
        UserDTO owner = mapper.readValue(invoice.getOwner(), UserDTO.class);
        text += "<body\n" +
                "    style=\"width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\">\n" +
                "<div class=\"es-wrapper-color\">\n" +
                "  <!--[if gte mso 9]>\n" +
                "  <v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\n" +
                "    <v:fill type=\"tile\" color=\"#f6f6f6\"></v:fill>\n" +
                "  </v:background>\n" +
                "  <![endif]-->\n" +
                "  <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"\n" +
                "         style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top\">\n" +
                "    <tr>\n" +
                "      <td style=\"padding:0;Margin:0;\">\n" +
                "\n" +
                "        <table class=\"es-header\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"\n" +
                "               style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\">\n" +
                "          <tr>\n" +
                "\n" +
                "            <td style=\"padding:0;Margin:0;\">\n" +
                "\n" +
                "              <table class=\"es-header-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\"\n" +
                "\n" +
                "                     style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:100%;\">\n" +
                "                <tr>\n" +
                "                  <td align=\"left\" style=\"padding:20px;Margin:0;\">\n" +
                "                    <table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\"\n" +
                "                           style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left;width:50%\">\n" +
                "                      <tr>\n" +
                "                        <td class=\"es-m-p0r es-m-p20b\" valign=\"top\" align=\"center\"\n" +
                "                            style=\"padding:0;Margin:0;width:180px;\">\n" +
                "                          <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\"\n" +
                "                                 style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\">\n" +
                "                            <tr>\n" +
                "                              <td align=\"left\" style=\"padding:0;Margin:0;\">\n" +
                "                              <td align=\"left\" style=\"padding:0;Margin:0\">\n" +
                "                                <div class=\"logo\">\n" +
                "                                  <img src=\"" + (company != null && company.getImage() != null ? company.getImage() : "https://media.oklyx.com/media/logo.png") + "\"\n" +
                "                                       alt=\"logo\">\n" +
                "                                </div>\n" +
                "                              </td>\n" +
                "                            </tr>\n" +
                "                          </table>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                    </table>\n" +
                "                </tr>\n" +
                "              </table>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>";
        text += "<table class=\"es-header\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"\n" +
                "               style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\">\n" +
                "          <tr>\n" +
                "\n" +
                "            <td style=\"padding:0;Margin:0;\">\n" +
                "\n" +
                "              <table class=\"es-header-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\"\n" +
                "\n" +
                "                     style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:100%;\">\n" +
                "                <tr>\n" +
                "                  <td align=\"left\" style=\"padding:20px;Margin:0;\">\n" +
                "                    <!--[if mso]>\n" +
                "\n" +
                "                    <table style=\"width:560px\" cellpadding=\"0\"\n" +
                "                           cellspacing=\"0\">\n" +
                "                      <tr>\n" +
                "                        <td style=\"width:180px\" valign=\"top\"><![endif]-->\n" +
                "                    <table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\"\n" +
                "                           style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left;width:50%\">\n" +
                "                      <tr>\n" +
                "                        <td class=\"es-m-p0r es-m-p20b\" valign=\"top\" align=\"center\"\n" +
                "                            style=\"padding:0;Margin:0;width:180px;\">\n" +
                "                          <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\"\n" +
                "                                 style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\">\n" +
                "                            <tr>\n" +
                "                              <td align=\"left\" style=\"padding:0;Margin:0;\">\n" +
                "                              <td align=\"left\" style=\"padding:0;Margin:0\">\n" +
                "                                <p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Inter', sans-serif;font-weight: 600;color:#223345;font-size:15px\">\n" +
                "                                  Invoice Number</p>\n" +
                "                                <p style=\"Margin:0;margin-top:8px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Inter', sans-serif;font-weight: 400;color: #223345;font-size:15px\">\n" +
                "                                  #" + invoice.getOrdinalNumber() + "</p>\n" +
                "                              </td>\n" +
                "                            </tr>\n" +
                "                          </table>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                    </table>\n" +
                "                    <table cellspacing=\"0\" cellpadding=\"0\" align=\"right\"\n" +
                "                           style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:25%\">\n" +
                "                      <tr>\n" +
                "\n" +
                "                        <td align=\"left\" style=\"padding:0;Margin:0;\">\n" +
                "                          <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\"\n" +
                "                                 style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\">\n" +
                "                            <tr>\n" +
                "                              <td align=\"right\" style=\"padding:0;Margin:0\">\n" +
                "                                <p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Inter', sans-serif;font-weight: 600;color:#223345;font-size:15px\">\n" +
                "                                  Due At</p>\n" +
                "                                <p style=\"Margin:0;margin-top:8px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:15px\">\n" +
                "                                  " + invoice.getDueAt().getMonth().name() + " " + invoice.getDueAt().getDayOfMonth() + ", " + invoice.getDueAt().getYear() + "</p>\n" +
                "                              </td>\n" +
                "                            </tr>\n" +
                "                          </table>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                    </table>\n" +
                "\n" +
                "                    <table cellspacing=\"0\" cellpadding=\"0\" align=\"right\"\n" +
                "                           style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:25%\">\n" +
                "                      <tr>\n" +
                "                        <td align=\"left\" style=\"padding:0;Margin:0;\">\n" +
                "                          <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\"\n" +
                "                                 style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                            <tr>\n" +
                "                              <td align=\"right\" style=\"padding:0;Margin:0;\">\n" +
                "                                <p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Inter', sans-serif;font-weight: 600;color:#223345;font-size:15px\">\n" +
                "                                  Invoiced At</p>\n" +
                "                                <p\n" +
                "                                    style=\"Margin:0;margin-top:8px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Inter', sans-serif;font-weight: 400;color:#223345;font-size:15px\">\n" +
                "                                  " + invoice.getInvoiceAt().getMonth().name() + " " + invoice.getInvoiceAt().getDayOfMonth() + ", " + invoice.getInvoiceAt().getYear() + "</p>\n" +
                "                              </td>\n" +
                "                            </tr>\n" +
                "                          </table>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                    </table>\n" +
                "                </tr>\n" +
                "              </table>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>";
        text += "<div class=\"line\"></div>\n" +
                "\n" +
                "        <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"\n" +
                "               style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed!important;width:100%\">\n" +
                "          <tr>\n" +
                "            <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "              <table class=\"es-content-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\"\n" +
                "                     style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:100%;\">\n" +
                "                <tr>\n" +
                "                  <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                "                    <!--[if mso]>\n" +
                "                    <table cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                      <tr>\n" +
                "                        <td valign=\"top\">\n" +
                "                    <![endif]-->\n" +
                "                    <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\"\n" +
                "                           style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left;width: 50%\">\n" +
                "                      <tr>\n" +
                "                        <td class=\"es-m-p0r es-m-p20b\" align=\"center\" style=\"padding:0;Margin:0;\">\n" +
                "                          <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                                 style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                            <tr>\n" +
                "                              <td align=\"left\" style=\"padding:0;Margin:0\">\n" +
                "                                <p\n" +
                "                                    style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Inter', sans-serif;font-weight: 600;color: #223345;font-size:15px\">\n" +
                "                                  Invoice From</p>\n" +
                "                                <p style=\"Margin:0;margin-top:20px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + owner.getFirstName() + " " + owner.getLastName() + "</p>\n" +
                "                                <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + owner.getEmail() + "</p>\n" +
                "                                <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + (company != null ? company.getName() : "-") + "</p>\n" +
                "                                <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + (company != null && company.getStreet() != null ? company.getStreet() : "-") + "</p>\n" +
                "                                <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + (company != null && company.getState() != null ? company.getState() : "-") + "</p>\n" +
                "                                <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + (company != null && company.getCity() != null ? company.getCity().getName() : "-") + "</p>\n" +
                "                                <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + (company != null && company.getCountry() != null ? company.getCountry().getName() : "-") + "</p>\n" +
                "                              </td>\n" +
                "                            </tr>\n" +
                "                          </table>\n" +
                "                        </td>\n" +
                "                        <td class=\"es-hidden\" style=\"padding:0;Margin:0;\"></td>\n" +
                "                      </tr>\n" +
                "                    </table>";
        text += "<table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\"\n" +
                "                           style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left;width: 50%\">\n" +
                "                      <tr>\n" +
                "                        <td align=\"right\" style=\"padding:0;Margin:0;\">\n" +
                "                          <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                                 style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                            <tr>\n" +
                "                              <td align=\"right\" style=\"padding:0;Margin:0;\">\n" +
                "                                <p\n" +
                "                                    style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 600;color:#223345;font-size:15px\">\n" +
                "                                  Invoice To</p>\n" +
                "                                <p style=\"Margin:0;margin-top:20px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + customer.getFirstName() + " " + customer.getLastName() + "</p>\n" +
                "                                <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + (customer.getCompanyName() != null ? customer.getCompanyName() : "-") + "</p>\n" +
                "                                <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + customer.getEmail() + "</p>\n" +
                "                                <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + (customer.getStreet() != null ? customer.getStreet() : "-") + "</p>\n" +
                "                                <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + (customer.getState() != null ? customer.getState() : "-") + "</p>\n" +
                "                                <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + (customer.getCity() != null ? customer.getCity().getName() : "-") + "</p>\n" +
                "                                <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                  " + (customer.getCountry() != null ? customer.getCountry().getName() : "-") + "</p>\n" +
                "                            </tr>\n" +
                "                          </table>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                    </table>\n" +
                "                    <!--[if mso]></td></tr></table><![endif]--></td>\n" +
                "                </tr>\n" +
                "              </table>";
        text += "<table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"\n" +
                "                     style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed!important;width:100%\">\n" +
                "                <tr>\n" +
                "                  <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                "                    <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"\n" +
                "                           style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                      <tr>\n" +
                "                        <td valign=\"top\" align=\"left\" style=\"padding:0;Margin:0;width:100%\">\n" +
                "                          <table class=\"invoice-table\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\"\n" +
                "                                 style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                            <tr class=\"invoice-head\">\n" +
                "                              <td width=\"50%\">\n" +
                "                                Name\n" +
                "                              </td>\n" +
                "                              <td align=\"center\" width=\"15%\">\n" +
                "                                Quantity\n" +
                "                              </td>\n" +
                "\n" +
                "                              <td width=\"15%\" align=\"center\">\n" +
                "                                Price\n" +
                "                              </td>\n" +
                "\n" +
                "                              <td width=\"20%\" align=\"center\">\n" +
                "                                Amount\n" +
                "                              </td>\n" +
                "                            </tr>\n" +
                "\n" +
                "                            <tbody class=\"invoice-body\">\n";
        for (InvoiceOffering invoiceOffering : invoice.getOfferings()) {
            text += "<tr>\n" +
                    "                              <td>\n" +
                    "                                <div>\n" +
                    "                                  <div class=\"title-offering\">\n" +
                    "                                    " + invoiceOffering.getOffering().getName() + "\n" +
                    "                                  </div>\n" +
                    "\n" +
                    "                                  <div class=\"description-offering\">\n" +
                    "                                    " + invoiceOffering.getOffering().getName() + "\n" +
                    "                                  </div>\n" +
                    "                                </div>\n" +
                    "                              </td>\n" +
                    "\n" +
                    "                              <td align=\"center\">\n" +
                    "                                " + invoiceOffering.getQuantity() + "\n" +
                    "                              </td>\n" +
                    "\n" +
                    "                              <td align=\"center\">\n" +
                    "                                " + invoice.getCurrency().getSymbol() + String.format("%,.2f", invoiceOffering.getPrice()) + "\n" +
                    "                              </td>\n" +
                    "\n" +
                    "                              <td align=\"center\">\n" +
                    "                                " + invoice.getCurrency().getSymbol() + String.format("%,.2f", (invoiceOffering.getPrice() * invoiceOffering.getQuantity())) + "\n" +
                    "                              </td>\n" +
                    "                            </tr>";
        }
        text += "<tr>\n" +
                "                              <td style=\"border-bottom: unset;\"></td>\n" +
                "\n" +
                "                              <td style=\"border-bottom: unset;\"></td>\n" +
                "\n" +
                "                              <td align=\"right\"\n" +
                "                                  style=\"border-bottom: unset;padding-top: 20px;padding-right: 40px; font-family: 'Inter',sans-serif;font-weight: 600;font-size: 16px; text-align: center;\">\n" +
                "                                Total:\n" +
                "                              </td>\n" +
                "                              <td style=\"border-bottom: unset;padding-top: 20px;padding-right: 40px; font-family: 'Inter', sans-serif;font-weight: 600;font-size: 16px; text-align: center;\">\n" +
                "                                " + invoice.getCurrency().getSymbol() + String.format("%,.2f", invoice.getTotal()) + "\n" +
                "                              </td>\n" +
                "                            </tr>\n" +
                "                            </tbody>\n" +
                "                          </table>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                    </table>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </table>";
        text += "<table class=\"es-footer\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"\n" +
                "                     style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed\n" +
                "              !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\">\n" +
                "                <tr>\n" +
                "                  <td align=\"center\" style=\"padding:0;Margin:0;\">\n" +
                "                    <table class=\"es-footer-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\"\n" +
                "                           style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:100%;\">\n" +
                "                      <tr>\n" +
                "                        <td align=\"left\" style=\"padding:20px;Margin:0;\">\n" +
                "                          <table cellpadding=\"0\" cellspacing=\"0\" width=\"50%\"\n" +
                "                                 style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\">\n" +
                "                            <tr>\n" +
                "                              <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                "                                <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                                       style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\">\n" +
                "                                  <tr>\n" +
                "                                    <td align=\"left\" width=\"100%\">\n" +
                "                                      <p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 600;color: #223345;font-size:15px;\">\n" +
                "                                        Payment information</p>\n" +
                "                                    </td>\n" +
                "                                  </tr>\n" +
                "\n" +
                "                                  <tr>\n" +
                "                                    <td align=\"left\" style=\"padding:0;Margin:0\">\n" +
                "                                      <p style=\"Margin:0;margin-top:20px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        Bank Name</p>\n" +
                "                                      <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        IBAN</p>\n" +
                "                                      <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        Swift/Bic</p>\n" +
                "                                      <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        Country</p>\n" +
                "                                      <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        City</p>\n" +
                "                                      <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        Street</p>\n" +
                "                                      <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        Postcode</p>\n" +
                "                                    </td>\n" +
                "\n" +
                "                                    <td align=\"right\" style=\"padding:0;Margin:0\">\n" +
                "                                      <p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 600;color: #223345;font-size:15px\"></p>\n" +
                "                                      <p style=\"Margin:0;margin-top:20px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        " + account.getName() + "</p>\n" +
                "                                      <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        " + account.getIban() + "</p>\n" +
                "                                      <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        " + account.getSwiftBic() + "</p>\n" +
                "                                      <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        " + (account.getCountry() != null ? account.getCountry().getName() : "-") + "</p>\n" +
                "                                      <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        " + (account.getCity() != null ? account.getCity().getName() : "-") + "</p>\n" +
                "                                      <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        " + (account.getStreet() != null ? account.getStreet() : "-") + "</p>\n" +
                "                                      <p style=\"Margin:0;margin-top:10px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                "                                        " + (account.getPostcode() != null ? account.getPostcode() : "-") + "</p>\n" +
                "                                    </td>\n" +
                "                                  </tr>\n" +
                "                                </table>\n" +
                "                              </td>\n" +
                "                            </tr>\n" +
                "                          </table>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                    </table>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </table>";
        if (invoice.getNotes() != null) {
            text += "<table class=\"es-footer\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"\n" +
                    "                     style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\">\n" +
                    "                <tr>\n" +
                    "                  <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                    "                    <table class=\"es-footer-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\"\n" +
                    "                           style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:100%\">\n" +
                    "                      <tr>\n" +
                    "                        <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                    "                          <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                    "                                 style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                    "                            <tr>\n" +
                    "                              <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;\">\n" +
                    "                                <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                    "                                       style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                    "                                  <tr>\n" +
                    "                                    <td align=\"left\" style=\"padding:0;Margin:0\">\n" +
                    "                                      <p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 600;color: #223345;font-size:15px\">\n" +
                    "                                        Notes</p>\n" +
                    "                                      <p style=\"Margin:0;margin-top:20px;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 400;color: #223345;font-size:14px\">\n" +
                    "                                        " + invoice.getNotes() + "</p>\n" +
                    "                                    </td>\n" +
                    "                                  </tr>\n" +
                    "                                </table>\n" +
                    "                              </td>\n" +
                    "                            </tr>\n" +
                    "                          </table>\n" +
                    "                        </td>\n" +
                    "                      </tr>\n" +
                    "                    </table>\n" +
                    "                  </td>\n" +
                    "                </tr>\n" +
                    "              </table>\n" +
                    "\n" +
                    "              <div class=\"line\"></div>\n" +
                    "\n" +
                    "              <table class=\"es-footer\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"\n" +
                    "                     style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\">\n" +
                    "                <tr>\n" +
                    "                  <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                    "                    <table class=\"es-footer-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\"\n" +
                    "                           style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:100%\">\n" +
                    "                      <tr>\n" +
                    "                        <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                    "                          <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                    "                                 style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                    "                            <tr>\n" +
                    "                              <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;\">\n" +
                    "                                <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                    "                                       style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                    "                                  <tr>\n" +
                    "                                    <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                    "                                      <p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family: 'Inter', sans-serif;font-weight: 600;color: #223345;font-size:15px\">\n" +
                    "                                        This invoice was generated through <a\n" +
                    "                                          style=\"text-decoration: unset; color: #0c7dd9\" href=\"https://oklyx.com/\"\n" +
                    "                                          target=\"_blank\">Oklyx.com</a>\n" +
                    "                                      </p>\n" +
                    "                                    </td>\n" +
                    "                                  </tr>\n" +
                    "                                </table>\n" +
                    "                              </td>\n" +
                    "                            </tr>\n" +
                    "                          </table>\n" +
                    "                        </td>\n" +
                    "                      </tr>\n" +
                    "                    </table>\n" +
                    "                  </td>\n" +
                    "                </tr>\n" +
                    "              </table>\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </td>\n" +
                    "    </tr>\n" +
                    "  </table>\n";
        }
         text += "</div>\n" +
                "</body>\n" +
                "</html>";
        String filename = "Invoice-" + invoice.getOrdinalNumber() + ".pdf";
        HtmlConverter.convertToPdf(text, new FileOutputStream(filename), new ConverterProperties());
        return filename;
    }
}
