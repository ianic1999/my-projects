package com.example.demo.model;

import com.example.demo.dto.ServiceDTO;
import com.example.demo.util.ServerUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotNull(message = "Title should not be null")
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "service", cascade = CascadeType.ALL)
    private ServiceTitle title;

    @NotNull(message = "Summary should not be null")
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "service", cascade = CascadeType.ALL)
    private ServiceSummary summary;

    @NotEmpty(message = "Image should not be null")
    @Pattern(regexp = ".+\\.(png|jpg|jpeg|webp|jpeg2000)", message = "Image should be in .png, .jpg, .jpeg, .webp or .jpeg2000 format")
    private String image;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    public ServiceDTO toDTO() {
        return ServiceDTO.builder()
                .id(id)
                .title(title.toDTO())
                .summary(summary.toDTO())
                .image(ServerUtil.getHostname() + "/" + image)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .createdBy(createdBy.toCreatedByDTO())
                .build();
    }
}
