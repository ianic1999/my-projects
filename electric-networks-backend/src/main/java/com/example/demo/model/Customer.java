package com.example.demo.model;

import com.example.demo.dto.CustomerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Contract number should not be null or empty")
    private String contractNumber;

    @NotEmpty(message = "Name should not be null or empty")
    private String fullName;

    @NotEmpty(message = "Address should not be null or empty")
    private String address;

    @NotEmpty(message = "Meter should not be null or empty")
    private String meter;

    private String phone;

    @NotNull(message = "Station should not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    private Station station;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Indication> indications;

    public CustomerDTO toDTO() {
        Optional<Indication> optional = indications.stream().max(Comparator.comparing(Indication::getSelectedAt));
        Double lastIndication = optional.isPresent() ? optional.get().getAmount() : null;
        return CustomerDTO.builder()
                .id(id)
                .contractNumber(contractNumber)
                .fullName(fullName)
                .address(address)
                .meter(meter)
                .phone(phone)
                .station(station.toDTO())
                .isCompleted(
                        indications.stream().anyMatch(
                                indication -> indication.getSelectedAt().getYear() == LocalDate.now().getYear()
                                    && indication.getSelectedAt().getMonth().equals(LocalDate.now().getMonth())
                                )
                )
                .lastIndication(lastIndication)
                .build();
    }
}
