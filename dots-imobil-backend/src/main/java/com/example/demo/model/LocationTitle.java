package com.example.demo.model;

import com.example.demo.dto.LocationTitleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LocationTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    private String ro;

    private String ru;

    private String en;

    @OneToOne(fetch = FetchType.LAZY)
    private Location location;

    public LocationTitleDTO toDTO() {
        return LocationTitleDTO.builder()
                .id(id)
                .ro(ro)
                .ru(ru)
                .en(en)
                .build();
    }
}
