package com.popcoadmin.content.dto.request;

import com.popcoadmin.content.enums.ContentTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ContentRequestDto {
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
}
