package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.dto.request.InvoiceRequest;
import com.example.demo.model.exception.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.dom4j.DocumentException;
import org.springframework.data.domain.Page;

import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public interface InvoiceService {
    Page<InvoiceDTO> get(int page, int perPage, UserDTO currentUser, String order, boolean ascending);
    InvoiceDTO getById(long id, UserDTO currentUser) throws InvoiceException, UserException;
    InvoiceDTO add(InvoiceRequest invoice, UserDTO currentUser) throws CustomerException, CompanyException, UserException, OfferingException, InvoiceException, BankAccountException, JsonProcessingException;
    InvoiceDTO update(InvoiceRequest invoice, UserDTO currentUser) throws CustomerException, CompanyException, InvoiceException, UserException, OfferingException, BankAccountException, JsonProcessingException;
    InvoiceDTO updateStatus(InvoiceChangeStatusDTO changeStatusDTO) throws InvoiceException;
    void remove(long id, UserDTO currentUser) throws UserException, InvoiceException;
    InvoiceStatisticsDTO getInvoiceStatistics(UserDTO currentUser);
    InvoiceDTO send(long id) throws InvoiceException, IOException, DocumentException, MessagingException;
    InvoicesPerYearResponseDTO getStatisticsPerYear(LocalDate date, UserDTO currentUser);
    InvoicesPerMonthResponseDTO getStatisticsPerMonth(LocalDate date, UserDTO currentUser);
    InvoicesPerWeekResponseDTO getStatisticsPerWeek(LocalDate date, UserDTO currentUser);
    OfferingStatisticsResponseDTO getOfferingStatistics(UserDTO currentUser);
    InvoicesPerYearResponseDTO getStatisticsPerYearForUser(LocalDate date, UserDTO currentUser, long userId) throws UserException;
    InvoicesPerMonthResponseDTO getStatisticsPerMonthForUser(LocalDate date, UserDTO currentUser, long userId) throws UserException;
    InvoicesPerWeekResponseDTO getStatisticsPerWeekForUser(LocalDate date, UserDTO currentUser, long userId) throws UserException;
    OfferingStatisticsResponseDTO getOfferingStatisticsForUser(UserDTO currentUser, long userId) throws UserException;
    InvoiceStatisticsDTO getInvoiceStatisticsForUser(UserDTO currentUser, long userId) throws UserException;
    ByteArrayInputStream downloadPdf(long invoiceId, UserDTO currentUser) throws InvoiceException, IOException;
}
