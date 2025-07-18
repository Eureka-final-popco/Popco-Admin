package com.popcoadmin.content.dto.response.genre;

import lombok.Data;

import java.util.List;

@Data
public class GenreListResponse {
    private List<GenreResponse> genres;
}
