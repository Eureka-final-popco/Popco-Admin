package com.popcoadmin.content.dto.response.watchprovider;

import lombok.Data;

@Data
public class WatchProvidersResponse {
    private Long id;
    private WatchProviderResults results;
}