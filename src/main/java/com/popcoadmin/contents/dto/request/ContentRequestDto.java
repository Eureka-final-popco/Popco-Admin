package com.popcoadmin.contents.dto.request;

import com.popcoadmin.contents.enums.ContentTypes;
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
