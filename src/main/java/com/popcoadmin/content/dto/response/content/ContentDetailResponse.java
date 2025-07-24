package com.popcoadmin.content.dto.response.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.popcoadmin.content.dto.response.ProductionCompanyResponse;
import com.popcoadmin.content.dto.response.genre.GenreResponse;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ContentDetailResponse extends ContentResponse {
    private Long budget;
    private Long revenue;
    private Integer runtime;
    private String status;
    private String tagline;
    private String homepage;

    @JsonProperty("imdb_id")
    private String imdbId;

    @JsonProperty("last_air_date")
    private LocalDate lastAirDate;

    private List<GenreResponse> genres;

    @JsonProperty("production_companies")
    private List<ProductionCompanyResponse> productionCompanies;
}
