package com.example.demo.model;


import com.example.demo.dto.EventDTO;
import com.example.demo.util.ServerUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    private long id;

    @NotEmpty(message = "Title should not be null or empty")
    @Size(max = 256, message = "Title should have at most 256 characters")
    private String title;

    @NotEmpty(message = "Body should not be null or empty")
    @Size(max = 1000000, message = "Body is too large")
    private String body;

    @NotEmpty(message = "Location should not be null or empty")
    @Size(max = 256, message = "Location should have at most 256 characters")
    private String location;

    @NotNull(message = "Due Date At should not be null")
    private LocalDateTime dueDateAt;

    @Size(max = 256, message = "Link should have at most 256 characters")
    private String link;

    @NotEmpty(message = "Image should not be null")
    @Pattern(regexp = ".+\\.(png|jpg|jpeg|webp|jpeg2000)", message = "Image should be in .png, .jpg, .jpeg, .webp or .jpeg2000 format")
    private String image;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    public EventDTO toDTO() {
        return EventDTO.builder()
                .id(id)
                .title(title)
                .body(body)
                .link(link)
                .location(location)
                .dueDateAt(dueDateAt)
                .image(ServerUtil.getHostname() + "/" + image)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .createdBy(createdBy.toCreatedByDTO())
                .build();
    }
}
