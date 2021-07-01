package com.example.demo.model;

import com.example.demo.dto.SettingDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @Column(unique = true)
    @Size(max = 256, message = "Setting key should have at most 256 characters")
    @NotEmpty
    private String key;

    @NotNull(message = "Publicity should be set for settings")
    private boolean isPublic;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "setting", cascade = CascadeType.ALL)
    private SettingValue value;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public SettingDTO toDTO() {
        return SettingDTO.builder()
                .id(id)
                .key(key)
                .isPublic(isPublic)
                .value(value.toDTO())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
