package com.popcoadmin.persona.entity;

import com.popcoadmin.persona.dto.request.PersonaRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String tag;
    private String imgPath;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany
    List<PersonaGenre> personaGenre;

    public static Persona from(PersonaRequestDto request) {
        return Persona.builder()
                .name(request.getName())
                .description(request.getDescription())
                .tag(request.getTag())
                .imgPath(request.getImgPath())
                .build();
    }

    public static Persona from(PersonaRequestDto request, Long id) {
        return Persona.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .tag(request.getTag())
                .imgPath(request.getImgPath())
                .build();
    }
}