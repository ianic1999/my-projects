package com.example.demo.model;

import com.example.demo.dto.SettingValueDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

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

    @NotEmpty(message = "Setting value EN should not be empty")
    @Column(columnDefinition = "TEXT")
    private String en;

    @Column(columnDefinition = "TEXT")
    private String ro;

    @Column(columnDefinition = "TEXT")
    private String ru;

    @Column(columnDefinition = "TEXT")
    private String de;

    @Column(columnDefinition = "TEXT")
    private String es;

    @OneToOne(fetch = FetchType.LAZY)
    private Setting setting;

    public SettingValueDTO toDTO() {
        return SettingValueDTO.builder()
                .en(en)
                .ru(ru)
                .ro(ro)
                .de(de)
                .es(es)
                .build();
    }

}
