package com.example.demo.model;

import com.example.demo.dto.SettingDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @Column(unique = true)
    @NotEmpty(message = "Setting key should not be empty")
    private String key;

    private boolean isPublic;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "setting", cascade = CascadeType.ALL)
    private SettingValue value;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public SettingDTO toDTO() {
        return SettingDTO.builder()
                .id(id)
                .isPublic(isPublic)
                .key(key)
                .value(value.toDTO())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
