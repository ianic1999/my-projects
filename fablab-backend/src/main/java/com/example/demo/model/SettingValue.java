package com.example.demo.model;

import com.example.demo.dto.SettingValueDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SettingValue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @Length(max = 1000000)
    private String ro;

    @Length(max = 1000000)
    private String en;

    @Length(max = 1000000)
    private String ru;

    @OneToOne(fetch = FetchType.LAZY)
    private Setting setting;

    public SettingValueDTO toDTO() {
        return SettingValueDTO.builder()
                .id(id)
                .ro(ro)
                .ru(ru)
                .en(en)
                .build();
    }
}
