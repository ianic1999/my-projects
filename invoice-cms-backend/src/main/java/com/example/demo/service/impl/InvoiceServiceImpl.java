package com.example.demo.service.impl;

import com.example.demo.dto.*;
import com.example.demo.dto.request.InvoiceOfferingRequest;
import com.example.demo.dto.request.InvoiceRequest;
import com.example.demo.model.*;
import com.example.demo.model.enums.Currency;
import com.example.demo.model.enums.InvoiceStatus;
import com.example.demo.model.enums.UserRole;
import com.example.demo.model.exception.*;
import com.example.demo.repository.*;
import com.example.demo.service.InvoiceService;
import com.example.demo.util.PDFGenerator;
import com.example.demo.util.aws.AwsSESService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final OfferingRepository offeringRepository;
    private final InvoiceOfferingRepository invoiceOfferingRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AwsSESService sesService;

    @Override
    public Page<InvoiceDTO> get(int page, int perPage, UserDTO currentUser, String order, boolean ascending) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, perPage)
                : ascending ? PageRequest.of(page - 1, perPage, Sort.by(order).ascending())
                    : PageRequest.of(page - 1, perPage, Sort.by(order).descending());
        return currentUser.getRole().equals(UserRole.USER.getKey())
            ? invoiceRepository.findByCreatedBy_Email(pageable, currentUser.getEmail()).map(Invoice::toDTO)
                : invoiceRepository.findAll(pageable).map(Invoice::toDTO);
    }

    @Override
    public InvoiceDTO getById(long id, UserDTO currentUser) throws InvoiceException, UserException {
        Invoice invoiceFromDB = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceException("Invoice with id " + id + " doesn't exist"));
        if (!(invoiceFromDB.getCreatedBy().getEmail().equals(currentUser.getEmail()) || currentUser.getRole().equals(UserRole.ADMIN.getKey())))
            throw new UserException("You try to get an invoice that is not yours");
        return invoiceFromDB.toDTO();
    }

    @Override
    @Transactional
    public InvoiceDTO add(InvoiceRequest invoice, UserDTO currentUser) throws CustomerException, CompanyException, UserException, OfferingException, InvoiceException, BankAccountException, JsonProcessingException {
        Customer customer = customerRepository.findById(invoice.getCustomerId()).orElseThrow(() -> new CustomerException("Customer with id " + invoice.getCustomerId() + " doesn't exist"));
        User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow(() -> new UserException("User with email " + currentUser.getEmail() + " doesn't exist"));
        BankAccount account = bankAccountRepository.findById(invoice.getBankAccountId()).orElseThrow(() -> new BankAccountException("Bank account with id " + invoice.getBankAccountId() + " doesn't exist"));
        if (invoice.getOfferings() == null || invoice.getOfferings().size() == 0)
            throw new InvoiceException("Invoice should have at least 1 offering");
        Company company = null;
        if (invoice.getDueAt().isBefore(invoice.getInvoiceAt())) {
            throw new InvoiceException("Due at date should be after invoiced at date");
        }
        if (invoice.getCompanyId() > 0) {
            company = companyRepository.findById(invoice.getCompanyId()).orElseThrow(() -> new CompanyException("Company with id " + invoice.getCompanyId() + " doesn't exist"));
            if (company.getAccounts().stream().noneMatch(bankAccount -> account.getId() == account.getId()))
                throw new BankAccountException("This bank account doesn't belong to the provided company");
        }
        if (invoice.getDeletedOfferings() != null) {
            if (invoice.getOfferings() != null) {
                invoice.setOfferings(
                        invoice.getOfferings().stream()
                            .filter(offering -> !invoice.getDeletedOfferings().contains(offering.getId()))
                            .collect(Collectors.toList())
                );
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Invoice invoiceToAdd = Invoice.builder()
                .offerings(new ArrayList<>())
                .company(company != null ? mapper.writeValueAsString(company.toDTO()) : null)
                .customer(mapper.writeValueAsString(customer.toDTO()))
                .notes(invoice.getNotes())
                .invoiceAt(invoice.getInvoiceAt())
                .bankAccount(mapper.writeValueAsString(account.toDTO()))
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .updatedAt(LocalDateTime.now(ZoneOffset.UTC))
                .dueAt(invoice.getDueAt())
                .currency(getCurrencyFromString(invoice.getCurrency()))
                .owner(mapper.writeValueAsString(currentUser))
                .createdBy(user)
                .total(0)
                .build();
        invoiceToAdd = invoiceRepository.save(invoiceToAdd);
        invoiceToAdd.setOrdinalNumber(invoiceToAdd.getId());
        for (InvoiceOfferingRequest offeringRequest: invoice.getOfferings()) {
            Offering offering = offeringRepository.findById(offeringRequest.getOfferingId()).orElseThrow(() -> new OfferingException("Offering with id " + offeringRequest.getOfferingId() + " doesn't exist"));
            InvoiceOffering invoiceOffering = InvoiceOffering.builder()
                    .price(offeringRequest.getPrice())
                    .quantity(offeringRequest.getQuantity())
                    .description(offeringRequest.getDescription())
                    .invoice(invoiceToAdd)
                    .offering(offering)
                    .build();
            invoiceOffering = invoiceOfferingRepository.save(invoiceOffering);
            invoiceToAdd.getOfferings().add(invoiceOffering);
            invoiceToAdd.setTotal(invoiceToAdd.getTotal() + invoiceOffering.getPrice() * invoiceOffering.getQuantity());
            offering.getInvoices().add(invoiceOffering);
        }
        return invoiceToAdd.toDTO();
    }

    @Override
    @Transactional
    public InvoiceDTO update(InvoiceRequest invoice, UserDTO currentUser) throws CustomerException, CompanyException, InvoiceException, UserException, OfferingException, BankAccountException, JsonProcessingException {
        Customer customer = customerRepository.findById(invoice.getCustomerId()).orElseThrow(() -> new CustomerException("Customer with id " + invoice.getCustomerId() + " doesn't exist"));
        BankAccount account = bankAccountRepository.findById(invoice.getBankAccountId()).orElseThrow(() -> new BankAccountException("Bank account with id " + invoice.getBankAccountId() + " doesn't exist"));
        Company company = null;
        if (invoice.getDueAt().isBefore(invoice.getInvoiceAt())) {
            throw new InvoiceException("Due at date should be after invoiced at date");
        }
        if (invoice.getCompanyId() > 0) {
            company = companyRepository.findById(invoice.getCompanyId()).orElseThrow(() -> new CompanyException("Company with id " + invoice.getCompanyId() + " doesn't exist"));
            if (company.getAccounts().stream().noneMatch(bankAccount -> account.getId() == account.getId()))
                throw new BankAccountException("This bank account doesn't belong to the provided company");
        }
        Invoice invoiceFromDB = invoiceRepository.findById(invoice.getId()).orElseThrow(() -> new InvoiceException("Invoice with id " + invoice.getId() + " doesn't exist"));
        if (!(invoiceFromDB.getCreatedBy().getEmail().equals(currentUser.getEmail()) || currentUser.getRole().equals(UserRole.ADMIN.getKey())))
            throw new UserException("You try to get an invoice that is not yours");
        if (!invoiceFromDB.getStatus().equals(InvoiceStatus.DRAFT))
            throw new InvoiceException("Invoice can't be updated after sending");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        invoiceFromDB.setCustomer(mapper.writeValueAsString(customer.toDTO()));
        invoiceFromDB.setCompany(company != null ? mapper.writeValueAsString(company.toDTO()) : null);
        invoiceFromDB.setNotes(invoice.getNotes());
        invoiceFromDB.setDueAt(invoice.getDueAt());
        invoiceFromDB.setBankAccount(mapper.writeValueAsString(account.toDTO()));
        invoiceFromDB.setInvoiceAt(invoice.getInvoiceAt());
        invoiceFromDB.setOrdinalNumber(invoice.getOrdinalNumber());
        invoiceFromDB.setCurrency(getCurrencyFromString(invoice.getCurrency()));
        if (invoice.getOfferings() != null) {
            for (InvoiceOfferingRequest offeringRequest: invoice.getOfferings()) {
                Offering offering = offeringRepository.findById(offeringRequest.getOfferingId()).orElseThrow(() -> new OfferingException("Offering with id " + offeringRequest.getOfferingId() + " doesn't exist"));
                if (offeringRequest.getId() == 0) {
                    InvoiceOffering invoiceOffering = InvoiceOffering.builder()
                            .price(offeringRequest.getPrice())
                            .quantity(offeringRequest.getQuantity())
                            .description(offeringRequest.getDescription())
                            .invoice(invoiceFromDB)
                            .offering(offering)
                            .build();
                    invoiceOffering = invoiceOfferingRepository.save(invoiceOffering);
                    invoiceFromDB.getOfferings().add(invoiceOffering);
                    offering.getInvoices().add(invoiceOffering);
                    invoiceFromDB.setTotal(invoiceFromDB.getTotal() + invoiceOffering.getPrice() * invoiceOffering.getQuantity());
                } else {
                    InvoiceOffering invoiceOffering = invoiceOfferingRepository.findById(offeringRequest.getId()).orElseThrow(() -> new InvoiceException("Invoice offering with id " + offeringRequest.getId() + " doesn't exist"));
                    double sum = invoiceOffering.getQuantity() * invoiceOffering.getPrice();
                    invoiceOffering.setDescription(offeringRequest.getDescription());
                    invoiceOffering.setQuantity(offeringRequest.getQuantity());
                    invoiceOffering.setPrice(offeringRequest.getPrice());
                    invoiceFromDB.setTotal(invoiceFromDB.getTotal() - sum + offeringRequest.getQuantity() * offeringRequest.getPrice());
                }
            }
        }
        if (invoice.getDeletedOfferings() != null) {
            for (long id : invoice.getDeletedOfferings()) {
                InvoiceOffering invoiceOffering = invoiceOfferingRepository.findById(id).orElseThrow(() -> new InvoiceException("Invoice offering with id " + id + " doesn't exist"));
                Offering offering = invoiceOffering.getOffering();
                int i = 0;
                while (offering.getInvoices().get(i).getId() != id)
                    i++;
                offering.getInvoices().remove(i);
                i = 0;
                while (invoiceFromDB.getOfferings().get(i).getId() != id && i < invoiceFromDB.getOfferings().size())
                    i++;
                if (i < invoiceFromDB.getOfferings().size())
                    invoiceFromDB.getOfferings().remove(i);
                invoiceOfferingRepository.deleteById(id);
                invoiceFromDB.setTotal(invoiceFromDB.getTotal() - invoiceOffering.getPrice() * invoiceOffering.getQuantity());
            }
        }
        invoiceFromDB.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return invoiceFromDB.toDTO();
    }

    @Override
    @Transactional
    public InvoiceDTO updateStatus(InvoiceChangeStatusDTO changeStatusDTO) throws InvoiceException {
        Invoice invoice = invoiceRepository.findById(changeStatusDTO.getId()).orElseThrow(() -> new InvoiceException("Invoice with id " + changeStatusDTO.getId() + " doesn't exist"));
        if (changeStatusDTO.getStatus().equals(InvoiceStatus.DRAFT.getKey()))
            invoice.setStatus(InvoiceStatus.DRAFT);
        else if (changeStatusDTO.getStatus().equals(InvoiceStatus.UNPAID.getKey()))
            invoice.setStatus(InvoiceStatus.UNPAID);
        else if (changeStatusDTO.getStatus().equals(InvoiceStatus.PAID.getKey()))
            invoice.setStatus(InvoiceStatus.PAID);
        else
            throw new InvoiceException("Invalid status");
        return invoice.toDTO();
    }

    @Override
    public void remove(long id, UserDTO currentUser) throws UserException, InvoiceException {
        Invoice invoiceFromDB = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceException("Invoice with id " + id + " doesn't exist"));
        if (!(invoiceFromDB.getCreatedBy().getEmail().equals(currentUser.getEmail()) || currentUser.getRole().equals(UserRole.ADMIN.getKey())))
            throw new UserException("You try to get an invoice that is not yours");
        invoiceRepository.deleteById(id);
    }

    @Override
    public InvoiceStatisticsDTO getInvoiceStatistics(UserDTO currentUser) {
        InvoiceStatisticsDTO statistics = new InvoiceStatisticsDTO();
        statistics.setOverdue(
                invoiceRepository.findByCreatedBy_Email(currentUser.getEmail()).stream()
                    .filter(invoice -> invoice.getStatus().equals(InvoiceStatus.UNPAID) && invoice.getDueAt().isBefore(LocalDate.now()))
                    .map(Invoice::getOfferings)
                    .map(offerings -> offerings.stream().map(
                            offering -> offering.getQuantity() * offering.getPrice()
                        )
                            .collect(Collectors.toList())
                    ).reduce((a, b) -> {a.addAll(b); return a;})
                .orElse(new ArrayList<>())
                .stream()
                .reduce(Double::sum)
                .orElse(0.0)
        );
        statistics.setDraft(
                invoiceRepository.findByCreatedBy_Email(currentUser.getEmail()).stream()
                    .filter(invoice -> invoice.getStatus().equals(InvoiceStatus.DRAFT))
                    .map(Invoice::getOfferings)
                        .map(offerings -> offerings.stream().map(
                                offering -> offering.getQuantity() * offering.getPrice()
                                )
                                        .collect(Collectors.toList())
                        ).reduce((a, b) -> {a.addAll(b); return a;})
                        .orElse(new ArrayList<>())
                        .stream()
                        .reduce(Double::sum)
                        .orElse(0.0)
        );
        statistics.setPaid(
                invoiceRepository.findByCreatedBy_Email(currentUser.getEmail()).stream()
                    .filter(invoice -> invoice.getStatus().equals(InvoiceStatus.PAID))
                    .map(Invoice::getOfferings)
                        .map(offerings -> offerings.stream().map(
                                offering -> offering.getQuantity() * offering.getPrice()
                                )
                                        .collect(Collectors.toList())
                        ).reduce((a, b) -> {a.addAll(b); return a;})
                        .orElse(new ArrayList<>())
                        .stream()
                        .reduce(Double::sum)
                        .orElse(0.0)
        );
        statistics.setUnpaid(
                invoiceRepository.findByCreatedBy_Email(currentUser.getEmail()).stream()
                .filter(invoice -> invoice.getStatus().equals(InvoiceStatus.UNPAID))
                .map(Invoice::getOfferings)
                .map(offerings -> offerings.stream().map(
                        offering -> offering.getQuantity() * offering.getPrice()
                        )
                        .collect(Collectors.toList())
                ).reduce((a, b) -> {a.addAll(b); return a;})
                .orElse(new ArrayList<>())
                .stream()
                .reduce(Double::sum)
                .orElse(0.0)
        );
        return statistics;
    }

    @Override
    @Transactional
    public InvoiceDTO send(long id) throws InvoiceException, IOException, MessagingException {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceException("Invoice with id " + id + " doesn't exist"));
        String filename = PDFGenerator.generatePDF(invoice);
        sesService.sendInvoice(invoice, filename);
        Files.deleteIfExists(Paths.get(filename));
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setLastSentAt(LocalDateTime.now());
        return invoice.toDTO();
    }

    @Override
    public InvoicesPerYearResponseDTO getStatisticsPerYear(LocalDate date, UserDTO currentUser) {
        InvoicesPerYearResponseDTO response = InvoicesPerYearResponseDTO.builder()
                .months(
                        invoiceRepository.findByCreatedBy_Email(currentUser.getEmail()).stream()
                                .filter(invoice -> invoice.getCreatedAt().getYear() == date.getYear())
                                .collect(Collectors.groupingBy(
                                        invoice -> invoice.getCreatedAt().getMonth()
                                ))
                                .values()
                                .stream()
                                .map(
                                        invoices -> InvoicesPerYearDTO.builder()
                                                .year(date.getYear())
                                                .month(invoices.get(0).getCreatedAt().getMonthValue())
                                                .total(
                                                        invoices.stream()
                                                                .map(Invoice::getTotal)
                                                                .reduce(0.0, Double::sum)
                                                )
                                                .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
        for (int i=1; i<13; i++) {
            if (!response.getMonths().stream().map(InvoicesPerYearDTO::getMonth).collect(Collectors.toList()).contains(i)) {
                response.getMonths().add(new InvoicesPerYearDTO(date.getYear(), i, 0.0));
            }
        }
        return response;
    }

    @Override
    public InvoicesPerMonthResponseDTO getStatisticsPerMonth(LocalDate date, UserDTO currentUser) {
        InvoicesPerMonthResponseDTO response = InvoicesPerMonthResponseDTO.builder()
                .days(
                        invoiceRepository.findByCreatedBy_Email(currentUser.getEmail()).stream()
                                .filter(invoice -> invoice.getCreatedAt().getYear() == date.getYear() && invoice.getCreatedAt().getMonthValue() == date.getMonthValue())
                                .collect(Collectors.groupingBy(
                                        invoice -> invoice.getCreatedAt().getDayOfMonth()
                                ))
                                .values()
                                .stream()
                                .map(
                                        invoices -> InvoicesPerMonthDTO.builder()
                                                .month(date.getMonthValue())
                                                .day(invoices.get(0).getCreatedAt().getDayOfMonth())
                                                .total(
                                                        invoices.stream()
                                                                .map(Invoice::getTotal)
                                                                .reduce(0.0, Double::sum)
                                                )
                                                .build()
                                ).collect(Collectors.toList())
                )
                .build();
        for (int i=1; i<=date.getMonth().length(date.getYear() % 4 == 0); i++) {
            if (!response.getDays().stream().map(InvoicesPerMonthDTO::getDay).collect(Collectors.toList()).contains(i)) {
                response.getDays().add(new InvoicesPerMonthDTO(date.getMonthValue(), i, 0.0));
            }
        }
        return response;
    }

    @Override
    public InvoicesPerWeekResponseDTO getStatisticsPerWeek(LocalDate date, UserDTO currentUser) {
        InvoicesPerWeekResponseDTO response = InvoicesPerWeekResponseDTO.builder()
                .days(
                        invoiceRepository.findByCreatedBy_Email(currentUser.getEmail()).stream()
                                .filter(invoice -> invoice.getCreatedAt().toLocalDate().isAfter(date.minusDays(date.getDayOfWeek().getValue())) && invoice.getCreatedAt().toLocalDate().isBefore(date.plusDays(8 - date.getDayOfWeek().getValue())))
                                .collect(Collectors.groupingBy(
                                        invoice -> invoice.getCreatedAt().getDayOfMonth()
                                ))
                                .values()
                                .stream()
                                .map(
                                        invoices -> InvoicesPerWeekDTO.builder()
                                                .month(date.getMonthValue())
                                                .day(invoices.get(0).getCreatedAt().getDayOfWeek().getValue())
                                                .total(
                                                        invoices.stream()
                                                                .map(Invoice::getTotal)
                                                                .reduce(0.0, Double::sum)
                                                )
                                                .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
        for (int i=1; i<8; i++) {
            if (!response.getDays().stream().map(InvoicesPerWeekDTO::getDay).collect(Collectors.toList()).contains(i)) {
                response.getDays().add(new InvoicesPerWeekDTO(date.getMonthValue(), i, 0.0));
            }
        }
        return response;
    }

    @Override
    public OfferingStatisticsResponseDTO getOfferingStatistics(UserDTO currentUser) {
        return OfferingStatisticsResponseDTO.builder()
                .offerings(
                        invoiceRepository.findByCreatedBy_Email(currentUser.getEmail())
                                .stream()
                                .map(Invoice::getOfferings)
                                .reduce(new ArrayList<>(), (a, b) -> {
                                    a.addAll(b);
                                    return a;
                                })
                                .stream()
                                .map(InvoiceOffering::getOffering)
                                .collect(Collectors.groupingBy(Offering::getId))
                                .values()
                                .stream()
                                .map(offerings -> new OfferingStatisticsDTO(
                                                offerings.get(0).toDTO(),
                                                offerings.size()
                                        )
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public InvoicesPerYearResponseDTO getStatisticsPerYearForUser(LocalDate date, UserDTO currentUser, long userId) throws UserException {
        if (!currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new UserException("Just admin users can do this operation");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User with id " + userId + " doesn't exist"));
        InvoicesPerYearResponseDTO response = InvoicesPerYearResponseDTO.builder()
                .months(
                        invoiceRepository.findByCreatedBy_Email(user.getEmail()).stream()
                                .filter(invoice -> invoice.getCreatedAt().getYear() == date.getYear())
                                .collect(Collectors.groupingBy(
                                        invoice -> invoice.getCreatedAt().getMonth()
                                ))
                                .values()
                                .stream()
                                .map(
                                        invoices -> InvoicesPerYearDTO.builder()
                                                .year(date.getYear())
                                                .month(invoices.get(0).getCreatedAt().getMonthValue())
                                                .total(
                                                        invoices.stream()
                                                                .map(Invoice::getTotal)
                                                                .reduce(0.0, Double::sum)
                                                )
                                                .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
        for (int i=1; i<13; i++) {
            if (!response.getMonths().stream().map(InvoicesPerYearDTO::getMonth).collect(Collectors.toList()).contains(i)) {
                response.getMonths().add(new InvoicesPerYearDTO(date.getYear(), i, 0.0));
            }
        }
        return response;
    }

    @Override
    public InvoicesPerMonthResponseDTO getStatisticsPerMonthForUser(LocalDate date, UserDTO currentUser, long userId) throws UserException {
        if (!currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new UserException("Just admin users can do this operation");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User with id " + userId + " doesn't exist"));
        InvoicesPerMonthResponseDTO response = InvoicesPerMonthResponseDTO.builder()
                .days(
                        invoiceRepository.findByCreatedBy_Email(user.getEmail()).stream()
                                .filter(invoice -> invoice.getCreatedAt().getYear() == date.getYear() && invoice.getCreatedAt().getMonthValue() == date.getMonthValue())
                                .collect(Collectors.groupingBy(
                                        invoice -> invoice.getCreatedAt().getDayOfMonth()
                                ))
                                .values()
                                .stream()
                                .map(
                                        invoices -> InvoicesPerMonthDTO.builder()
                                                .month(date.getMonthValue())
                                                .day(invoices.get(0).getCreatedAt().getDayOfMonth())
                                                .total(
                                                        invoices.stream()
                                                                .map(Invoice::getTotal)
                                                                .reduce(0.0, Double::sum)
                                                )
                                                .build()
                                ).collect(Collectors.toList())
                )
                .build();
        for (int i=1; i<=date.getMonth().length(date.getYear() % 4 == 0); i++) {
            if (!response.getDays().stream().map(InvoicesPerMonthDTO::getDay).collect(Collectors.toList()).contains(i)) {
                response.getDays().add(new InvoicesPerMonthDTO(date.getMonthValue(), i, 0.0));
            }
        }
        return response;
    }

    @Override
    public InvoicesPerWeekResponseDTO getStatisticsPerWeekForUser(LocalDate date, UserDTO currentUser, long userId) throws UserException {
        if (!currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new UserException("Just admin users can do this operation");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User with id " + userId + " doesn't exist"));
        InvoicesPerWeekResponseDTO response = InvoicesPerWeekResponseDTO.builder()
                .days(
                        invoiceRepository.findByCreatedBy_Email(user.getEmail()).stream()
                                .filter(invoice -> invoice.getCreatedAt().toLocalDate().isAfter(date.minusDays(date.getDayOfWeek().getValue())) && invoice.getCreatedAt().toLocalDate().isBefore(date.plusDays(8 - date.getDayOfWeek().getValue())))
                                .collect(Collectors.groupingBy(
                                        invoice -> invoice.getCreatedAt().getDayOfMonth()
                                ))
                                .values()
                                .stream()
                                .map(
                                        invoices -> InvoicesPerWeekDTO.builder()
                                                .month(date.getMonthValue())
                                                .day(invoices.get(0).getCreatedAt().getDayOfWeek().getValue())
                                                .total(
                                                        invoices.stream()
                                                                .map(Invoice::getTotal)
                                                                .reduce(0.0, Double::sum)
                                                )
                                                .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
        for (int i=1; i<8; i++) {
            if (!response.getDays().stream().map(InvoicesPerWeekDTO::getDay).collect(Collectors.toList()).contains(i)) {
                response.getDays().add(new InvoicesPerWeekDTO(date.getMonthValue(), i, 0.0));
            }
        }
        return response;
    }

    @Override
    public OfferingStatisticsResponseDTO getOfferingStatisticsForUser(UserDTO currentUser, long userId) throws UserException {
        if (!currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new UserException("Just admin users can do this operation");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User with id " + userId + " doesn't exist"));
        return OfferingStatisticsResponseDTO.builder()
                .offerings(
                        invoiceRepository.findByCreatedBy_Email(user.getEmail())
                                .stream()
                                .map(Invoice::getOfferings)
                                .reduce(new ArrayList<>(), (a, b) -> {
                                    a.addAll(b);
                                    return a;
                                })
                                .stream()
                                .map(InvoiceOffering::getOffering)
                                .collect(Collectors.groupingBy(Offering::getId))
                                .values()
                                .stream()
                                .map(offerings -> new OfferingStatisticsDTO(
                                                offerings.get(0).toDTO(),
                                                offerings.size()
                                        )
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public InvoiceStatisticsDTO getInvoiceStatisticsForUser(UserDTO currentUser, long userId) throws UserException {
        if (!currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new UserException("Just admin users can do this operation");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User with id " + userId + " doesn't exist"));
        InvoiceStatisticsDTO statistics = new InvoiceStatisticsDTO();
        statistics.setOverdue(
                invoiceRepository.findByCreatedBy_Email(user.getEmail()).stream()
                        .filter(invoice -> invoice.getStatus().equals(InvoiceStatus.UNPAID) && invoice.getDueAt().isBefore(LocalDate.now()))
                        .map(Invoice::getOfferings)
                        .map(offerings -> offerings.stream().map(
                                offering -> offering.getQuantity() * offering.getPrice()
                                )
                                        .collect(Collectors.toList())
                        ).reduce((a, b) -> {a.addAll(b); return a;})
                        .orElse(new ArrayList<>())
                        .stream()
                        .reduce(Double::sum)
                        .orElse(0.0)
        );
        statistics.setDraft(
                invoiceRepository.findByCreatedBy_Email(user.getEmail()).stream()
                        .filter(invoice -> invoice.getStatus().equals(InvoiceStatus.DRAFT))
                        .map(Invoice::getOfferings)
                        .map(offerings -> offerings.stream().map(
                                offering -> offering.getQuantity() * offering.getPrice()
                                )
                                        .collect(Collectors.toList())
                        ).reduce((a, b) -> {a.addAll(b); return a;})
                        .orElse(new ArrayList<>())
                        .stream()
                        .reduce(Double::sum)
                        .orElse(0.0)
        );
        statistics.setPaid(
                invoiceRepository.findByCreatedBy_Email(user.getEmail()).stream()
                        .filter(invoice -> invoice.getStatus().equals(InvoiceStatus.PAID))
                        .map(Invoice::getOfferings)
                        .map(offerings -> offerings.stream().map(
                                offering -> offering.getQuantity() * offering.getPrice()
                                )
                                        .collect(Collectors.toList())
                        ).reduce((a, b) -> {a.addAll(b); return a;})
                        .orElse(new ArrayList<>())
                        .stream()
                        .reduce(Double::sum)
                        .orElse(0.0)
        );
        return statistics;
    }

    @Override
    public ByteArrayInputStream downloadPdf(long invoiceId, UserDTO currentUser) throws InvoiceException, IOException {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new InvoiceException("Invoice with id " + invoiceId + " doesn't exist"));
        if (!currentUser.getEmail().equals(invoice.getCreatedBy().getEmail()) && !currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new InvoiceException("You try to get an invoice that is not yours");
        String filename = PDFGenerator.generatePDF(invoice);
        File file = new File(filename);
        byte[] bytes = Files.readAllBytes(file.toPath());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Files.deleteIfExists(Paths.get(filename));
        return inputStream;
    }

    private Currency getCurrencyFromString(String currencyString) throws InvoiceException {
        for (Currency currency : Currency.values()) {
            if (currency.getKey().equals(currencyString))
                return currency;
        }
        throw new InvoiceException("Invalid currency: " + currencyString);
    }
}
