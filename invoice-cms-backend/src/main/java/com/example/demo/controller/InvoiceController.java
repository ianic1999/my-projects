package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.request.InvoiceRequest;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.enums.Currency;
import com.example.demo.model.exception.*;
import com.example.demo.service.InvoiceService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.dom4j.DocumentException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin("*")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<InvoiceDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "15") int perPage,
                                                                @RequestParam(required = false) String order) throws UserException {
        return ResponseEntity.ok(
                new PaginatedResponse<>(invoiceService.get(page, perPage, userService.getCurrentUser(), order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-")))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<InvoiceDTO>> getById(@PathVariable long id) throws InvoiceException, UserException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.getById(id, userService.getCurrentUser()))
        );
    }

    @GetMapping("/counters")
    public ResponseEntity<Response<InvoiceStatisticsDTO>> getStatistics() throws UserException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.getInvoiceStatistics(userService.getCurrentUser()))
        );
    }

    @GetMapping("/counters/{userId}")
    public ResponseEntity<Response<InvoiceStatisticsDTO>> getStatisticsForUser(@PathVariable long userId) throws UserException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.getInvoiceStatisticsForUser(userService.getCurrentUser(), userId))
        );
    }

    @GetMapping("/statistics/year")
    public ResponseEntity<Response<InvoicesPerYearResponseDTO>> getAllPerYear(@RequestParam(required = false) String date) throws UserException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.getStatisticsPerYear(date != null ? LocalDate.parse(date) : LocalDate.now(), userService.getCurrentUser()))
        );
    }

    @GetMapping("/statistics/month")
    public ResponseEntity<Response<InvoicesPerMonthResponseDTO>> getAllPerMonth(@RequestParam(required = false) String date) throws UserException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.getStatisticsPerMonth(date != null ? LocalDate.parse(date) : LocalDate.now(), userService.getCurrentUser()))
        );
    }

    @GetMapping("/statistics/week")
    public ResponseEntity<Response<InvoicesPerWeekResponseDTO>> getAllPerWeek(@RequestParam(required = false) String date) throws UserException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.getStatisticsPerWeek(date != null ? LocalDate.parse(date) : LocalDate.now(), userService.getCurrentUser()))
        );
    }

    @GetMapping("/statistics/offerings")
    public ResponseEntity<Response<OfferingStatisticsResponseDTO>> getOfferingStatistics() throws UserException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.getOfferingStatistics(userService.getCurrentUser()))
        );
    }

    @GetMapping("/statistics/year/{userId}")
    public ResponseEntity<Response<InvoicesPerYearResponseDTO>> getAllPerYearForUser(@PathVariable long userId,
                                                                                     @RequestParam(required = false) String date) throws UserException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.getStatisticsPerYearForUser(date != null ? LocalDate.parse(date) : LocalDate.now(), userService.getCurrentUser(), userId))
        );
    }

    @GetMapping("/statistics/month/{userId}")
    public ResponseEntity<Response<InvoicesPerMonthResponseDTO>> getAllPerMonthForUser(@PathVariable long userId,
                                                                                       @RequestParam(required = false) String date) throws UserException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.getStatisticsPerMonthForUser(date != null ? LocalDate.parse(date) : LocalDate.now(), userService.getCurrentUser(), userId))
        );
    }

    @GetMapping("/statistics/week/{userId}")
    public ResponseEntity<Response<InvoicesPerWeekResponseDTO>> getAllPerWeekForUser(@PathVariable long userId,
                                                                                     @RequestParam(required = false) String date) throws UserException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.getStatisticsPerWeekForUser(date != null ? LocalDate.parse(date) : LocalDate.now(), userService.getCurrentUser(), userId))
        );
    }

    @GetMapping("/statistics/offerings/{userId}")
    public ResponseEntity<Response<OfferingStatisticsResponseDTO>> getOfferingStatisticsForUser(@PathVariable long userId) throws UserException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.getOfferingStatisticsForUser(userService.getCurrentUser(), userId))
        );
    }

    @GetMapping("/currencies")
    public ResponseEntity<Response<List<CurrencyDTO>>> getCurrencies(@RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new Response<>(
                        Arrays.stream(Currency.values())
                                .map(Currency::toDTO)
                                .filter(currency -> currency.getCode().contains(search))
                                .collect(Collectors.toList())
                )
        );
    }

    @PostMapping
    public ResponseEntity<Response<InvoiceDTO>> add(@RequestBody InvoiceRequest invoice) throws CompanyException, CustomerException, UserException, OfferingException, InvoiceException, BankAccountException, JsonProcessingException {
        return new ResponseEntity<>(
                new Response<>(invoiceService.add(invoice, userService.getCurrentUser())),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/change_status")
    public ResponseEntity<Response<InvoiceDTO>> changeStatus(@RequestBody InvoiceChangeStatusDTO changeStatusDTO) throws InvoiceException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.updateStatus(changeStatusDTO))
        );
    }

    @PostMapping("/send/{id}")
    public ResponseEntity<Response<InvoiceDTO>> send(@PathVariable long id) throws InvoiceException, IOException, DocumentException, MessagingException {
        return ResponseEntity.ok(
                new Response<>(invoiceService.send(id))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<InvoiceDTO>> update(@PathVariable long id,
                                                       @RequestBody InvoiceRequest invoice) throws InvoiceException, CustomerException, CompanyException, UserException, OfferingException, BankAccountException, JsonProcessingException {
        invoice.setId(id);
        return ResponseEntity.ok(
                new Response<>(invoiceService.update(invoice, userService.getCurrentUser()))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws UserException, InvoiceException {
        invoiceService.remove(id, userService.getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadInvoicePdf(@PathVariable long id) throws UserException, InvoiceException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=Invoice.pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(invoiceService.downloadPdf(id, userService.getCurrentUser())));
    }
}
