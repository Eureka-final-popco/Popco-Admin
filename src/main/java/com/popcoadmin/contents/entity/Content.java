package com.popcoadmin.contents.entity;

import com.popcoadmin.contents.dto.request.ContentRequestDto;
import com.popcoadmin.contents.enums.ContentTypes;
import com.popcoadmin.persona.dto.request.PersonaRequestDto;
import com.popcoadmin.persona.entity.Persona;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "content")
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    @Id
    @Column(name = "content_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    private String title;
    private String overview;
    private BigDecimal ratingAverage;
    private LocalDateTime releaseDate;
    private Integer ratingCount;
    private String backdropPath;

    @Enumerated(EnumType.STRING)
    private ContentTypes type;
    private String posterPath;
    private String trailerPath;
    private Integer runtime;


    public static Content from(ContentRequestDto request) {
        return Content.builder()
                .title(request.getTitle())
                .overview(request.getOverview())
                .ratingAverage(request.getRatingAverage())
                .releaseDate(request.getReleaseDate())
                .ratingCount(request.getRatingCount())
                .backdropPath(request.getBackdropPath())
                .type(request.getType())
                .posterPath(request.getPosterPath())
                .trailerPath(request.getTrailerPath())
                .runtime(request.getRuntime())
                .build();
    }
}