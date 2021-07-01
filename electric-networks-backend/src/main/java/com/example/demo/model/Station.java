package com.example.demo.model;

import com.example.demo.dto.StationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Name should not be null or empty")
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "station", cascade = CascadeType.ALL)
    private List<Customer> customers;

    private Integer nrOfOrder;

    public StationDTO toDTO() {
        return StationDTO.builder()
                .id(id)
                .name(name)
                .nrOfOrder(nrOfOrder)
                .build();
    }
}
