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

@Table(name = "personas")
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Persona {
    @Id
    @Column(name = "persona_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personaId;

    private String name;
    private String description;
    private String tag;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL)
    List<PersonaGenre> personaGenre;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL)
    List<PersonaDetail> personaDetail;

    public static Persona from(PersonaRequestDto request) {
        return Persona.builder()
                .description(request.getDescription())
                .tag(request.getTag())
                .build();
    }

    public static Persona from(PersonaRequestDto request, Long id) {
        return Persona.builder()
                .personaId(id)
                .description(request.getDescription())
                .tag(request.getTag())
                .build();
    }
}