package com.popcoadmin.content.dto.response.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.popcoadmin.content.dto.response.credit.CreditsResponse;
import com.popcoadmin.content.dto.response.videos.VideosResponse;
import com.popcoadmin.content.dto.response.watchprovider.WatchProvidersResponse;
import lombok.Data;

@Data
public class ContentFullDetailResponse extends ContentDetailResponse {
    // 기본 상세 정보는 ContentDetailResponse에서 상속

    // 추가 정보들
    private CreditsResponse credits;
    private VideosResponse videos;

    @JsonProperty("watch/providers")
    private WatchProvidersResponse watchProviders;
}
