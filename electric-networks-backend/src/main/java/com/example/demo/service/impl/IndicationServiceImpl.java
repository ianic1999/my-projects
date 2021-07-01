package com.example.demo.service.impl;

import com.example.demo.dto.IndicationDTO;
import com.example.demo.dto.IndicationsPerMonthDTO;
import com.example.demo.dto.IndicationsPerMonthResponseDTO;
import com.example.demo.dto.request.IndicationRequest;
import com.example.demo.model.Customer;
import com.example.demo.model.Indication;
import com.example.demo.model.exception.CustomerException;
import com.example.demo.model.exception.IndicationException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.IndicationRepository;
import com.example.demo.service.IndicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndicationServiceImpl implements IndicationService {
    private final IndicationRepository indicationRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Page<IndicationDTO> getAll(int page, int perPage, long customerId, int year) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        if (customerId == 0)
            return indicationRepository.findAll(pageable).map(Indication::toDTO);
        else {
            if (year == 0)
                return indicationRepository.findByCustomer_Id(customerId, pageable).map(Indication::toDTO);
            else {
                LocalDate start = LocalDate.of(year, 1, 1);
                LocalDate end = LocalDate.of(year, 12, 31);
                return indicationRepository.findByCustomer_IdAndSelectedAtBetween(customerId, start, end, pageable).map(Indication::toDTO);
            }
        }
    }

    @Override
    public IndicationDTO getById(long id) throws IndicationException {
        return indicationRepository.findById(id).orElseThrow(() -> new IndicationException("Indication with id " + id + " doesn't exist")).toDTO();

    }

    @Override
    public IndicationDTO add(IndicationRequest indication) throws CustomerException, IndicationException {
        Customer customer = customerRepository.findById(indication.getCustomer()).orElseThrow(() -> new CustomerException("Customer with id " + indication.getCustomer() + " doesn't exist"));
        if (customer.getIndications().stream().anyMatch(ind -> ind.getSelectedAt().getMonth().equals(indication.getSelectedAt().getMonth()) && ind.getSelectedAt().getYear() == indication.getSelectedAt().getYear()))
            throw new IndicationException("Deja exista o indicatie pentru aceasta persoana, in aceasta luna");
        Indication indicationToAdd = Indication.builder()
                .createdAt(LocalDate.now())
                .selectedAt(indication.getSelectedAt())
                .amount(indication.getAmount())
                .customer(customer)
                .build();
        return indicationRepository.save(indicationToAdd).toDTO();
    }

    @Override
    @Transactional
    public IndicationDTO update(IndicationRequest indication) throws CustomerException, IndicationException {
        Customer customer = customerRepository.findById(indication.getCustomer()).orElseThrow(() -> new CustomerException("Customer with id " + indication.getCustomer() + " doesn't exist"));
        Indication indicationFromDB = indicationRepository.findById(indication.getId()).orElseThrow(() -> new IndicationException("Indication with id " + indication.getId() + " doesn't exist"));
        indicationFromDB.setAmount(indication.getAmount());
        indicationFromDB.setSelectedAt(indication.getSelectedAt());
        indicationFromDB.setCustomer(customer);
        return indicationFromDB.toDTO();
    }

    @Override
    public void remove(long id) {
        indicationRepository.deleteById(id);
    }

    @Override
    public IndicationDTO getLast(long customerId) throws IndicationException {
        return indicationRepository.findByCustomer_Id(customerId).stream()
                .max(Comparator.comparing(Indication::getSelectedAt))
                .orElseThrow(() -> new IndicationException("Nu exista indicatii pentru persoana data"))
                .toDTO();
    }

    @Override
    public IndicationsPerMonthResponseDTO getIndicationsPerMonth() {
        return new IndicationsPerMonthResponseDTO(
                indicationRepository.findAll().stream()
                .collect(
                        Collectors.groupingBy(
                                indication -> Pair.of(indication. getSelectedAt().getYear(), indication.getSelectedAt().getMonthValue())
                        )
                )
                .entrySet()
                .stream()
                .map(entry -> IndicationsPerMonthDTO.builder()
                        .year(entry.getKey().getFirst())
                        .month(entry.getKey().getSecond())
                        .total(entry.getValue().stream().map(Indication::getAmount).reduce(Double::sum).orElse(0.0))
                        .build()
                ).collect(Collectors.toList())
        );
    }
}
