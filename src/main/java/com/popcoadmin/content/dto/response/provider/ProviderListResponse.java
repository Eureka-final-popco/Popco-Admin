package com.popcoadmin.content.dto.response.provider;

import lombok.Data;

import java.util.List;

@Data
public class ProviderListResponse {
    private List<ProviderResponse> results;
}
