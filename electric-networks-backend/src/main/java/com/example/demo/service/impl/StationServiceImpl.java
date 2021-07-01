package com.example.demo.service.impl;

import com.example.demo.dto.StationDTO;
import com.example.demo.dto.StationWithCustomersDTO;
import com.example.demo.model.Station;
import com.example.demo.model.exception.StationException;
import com.example.demo.repository.StationRepository;
import com.example.demo.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {
    private final StationRepository stationRepository;

    @Override
    public Page<StationDTO> getAll(int page, int perPage, String field, Sort.Direction order) {
        Pageable pageable = field == null ? PageRequest.of(page - 1, perPage)
            : PageRequest.of(page - 1, perPage, Sort.by(new Sort.Order(order, field, Sort.NullHandling.NULLS_LAST).nullsLast()));
        return stationRepository.findAll(pageable).map(Station::toDTO);
    }

    @Override
    public StationDTO getById(long id) throws StationException {
        return stationRepository.findById(id).orElseThrow(() -> new StationException("Station with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    public StationDTO add(StationDTO station) {
        Station stationToAdd = Station.builder()
                .name(station.getName())
                .customers(new ArrayList<>())
                .nrOfOrder(station.getNrOfOrder())
                .build();
        return stationRepository.save(stationToAdd).toDTO();
    }

    @Override
    @Transactional
    public StationDTO update(StationDTO station) throws StationException {
        Station stationFromDB = stationRepository.findById(station.getId()).orElseThrow(() -> new StationException("Station with id " + station.getId() + " doesn't exist"));
        stationFromDB.setName(station.getName());
        stationFromDB.setNrOfOrder(station.getNrOfOrder());
        return stationFromDB.toDTO();
    }

    @Override
    public void remove(long id) throws StationException {
        Station station = stationRepository.findById(id).orElseThrow(() -> new StationException("Nu exista statie cu id-ul " + id));
        if (station.getCustomers().size() > 0)
            throw new StationException("Statia data nu poate fi stearsa, fiindca are clienti");
        stationRepository.deleteById(id);
    }

    @Override
    public Page<StationWithCustomersDTO> getAllWithCustomers(int page, int perPage, int month, String field, Sort.Direction order) {
        Pageable pageable = field == null ? PageRequest.of(page - 1, perPage)
                : PageRequest.of(page - 1, perPage, Sort.by(new Sort.Order(order, field, Sort.NullHandling.NULLS_LAST).nullsLast()));
        return stationRepository.findAll(pageable).map(station ->
                StationWithCustomersDTO.builder()
                        .id(station.getId())
                        .name(station.getName())
                        .nrOfOrder(station.getNrOfOrder())
                        .customers(station.getCustomers().size())
                        .indications(
                                (int) station.getCustomers().stream()
                                        .filter(customer ->
                                                customer.getIndications().stream()
                                                        .anyMatch(indication ->
                                                                indication.getSelectedAt().getMonth().getValue() == month
                                                                    && indication.getSelectedAt().getYear() == LocalDate.now().getYear()
                                                        )
                                        )
                                        .count()
                        )
                .build()
        );
    }
}
