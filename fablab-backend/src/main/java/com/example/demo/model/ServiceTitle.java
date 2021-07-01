package com.example.demo.model;

import com.example.demo.dto.ServiceSummaryDTO;
import com.example.demo.dto.ServiceTitleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ServiceTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @Size(max = 256, message = "Title should have at most 256 characters")
    private String ro;

    @Size(max = 256, message = "Title should have at most 256 characters")
    private String ru;

    @Size(max = 256, message = "Title should have at most 256 characters")
    private String en;

    @OneToOne(fetch = FetchType.LAZY)
    private Service service;

    public ServiceTitleDTO toDTO() {
        return ServiceTitleDTO.builder()
                .ro(ro)
                .ru(ru)
                .en(en)
                .build();
    }
}
