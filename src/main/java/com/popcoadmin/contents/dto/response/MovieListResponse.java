package com.popcoadmin.contents.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class MovieListResponse {
    private Integer page;

    @JsonProperty("results")
    private List<MovieDto> results;
}

