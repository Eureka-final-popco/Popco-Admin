package com.popcoadmin.content.dto.response.credit;

import lombok.Data;

import java.util.List;

@Data
public class CreditsResponse {
    private Long id;
    private List<CastResponse> cast;
    private List<CrewResponse> crew;
}
