package com.popcoadmin.content.entity;

import com.popcoadmin.content.dto.request.ContentRequestDto;
import com.popcoadmin.content.enums.ContentTypes;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    private String title;
    private String overview;
    private Float rating_average;
    private LocalDateTime releaseDate;
    private Integer ratingCount;
    private String backdropPath;
    private ContentTypes type;
    private String posterPath;
    private String trailerPath;
    private Integer runtime;

    public static Content from(ContentRequestDto request) {
        return Content.builder()
                .title(request.getTitle())
                .overview(request.getOverview())
                .rating_average(request.getRating_average())
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
