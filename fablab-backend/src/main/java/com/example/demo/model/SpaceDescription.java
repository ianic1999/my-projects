package com.example.demo.model;

import com.example.demo.dto.SpaceDescriptionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SpaceDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @Size(max = 10000, message = "Description should have at most 10000 characters")
    private String ro;

    @Size(max = 10000, message = "Description should have at most 10000 characters")
    private String ru;

    @Size(max = 10000, message = "Description should have at most 10000 characters")
    private String en;

    @OneToOne(fetch = FetchType.LAZY)
    private Space space;

    public SpaceDescriptionDTO toDTO() {
        return SpaceDescriptionDTO.builder()
                .ro(ro)
                .ru(ru)
                .en(en)
                .build();
    }
}
