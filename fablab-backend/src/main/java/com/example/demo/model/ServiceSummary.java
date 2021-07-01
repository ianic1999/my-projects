package com.example.demo.model;

import com.example.demo.dto.ServiceSummaryDTO;
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
@Data
@Builder
public class ServiceSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @Size(max = 1024, message = "Summary should have at most 1024 characters")
    private String ro;

    @Size(max = 1024, message = "Summary should have at most 1024 characters")
    private String ru;

    @Size(max = 1024, message = "Summary should have at most 1024 characters")
    private String en;

    @OneToOne(fetch = FetchType.LAZY)
    private Service service;

    public ServiceSummaryDTO toDTO() {
        return ServiceSummaryDTO.builder()
                .ro(ro)
                .ru(ru)
                .en(en)
                .build();
    }
}
