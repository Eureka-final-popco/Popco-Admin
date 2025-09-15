package com.popcoadmin.apiclient;

import com.popcoadmin.content.dto.response.content.ContentFullDetailResponse;
import com.popcoadmin.content.dto.response.content.ContentPageResponse;
import com.popcoadmin.content.dto.response.genre.GenreListResponse;
import com.popcoadmin.content.dto.response.provider.ProviderListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class TmdbTvApiClient {

    private final WebClient webClient;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    @Value("${tmdb.api.max-retries}")
    private int maxRetries;

    @Value("${tmdb.api.retry-delay}")
    private long retryDelay;

    public Mono<GenreListResponse> getGenres() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/genre/tv/list")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .bodyToMono(GenreListResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching genres: {}", error.getMessage()));
    }

    // 모든 TV Provider 목록 가져오기
    public Mono<ProviderListResponse> getAllTvProviders() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/watch/providers/tv")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .queryParam("watch_region", "KR")
                        .build())
                .retrieve()
                .bodyToMono(ProviderListResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching tv providers: {}", error.getMessage()));
    }

    public Mono<ContentPageResponse> getOnTheAirTvs(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/on_the_air")
                        .queryParam("api_key", apiKey)
                        .queryParam("page", page)
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching on the air tvs page {}: {}", page, error.getMessage()));
    }

    public Mono<ContentPageResponse> getPopularTvs(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/popular")
                        .queryParam("api_key", apiKey)
                        .queryParam("page", page)
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching popular tvs page {}: {}", page, error.getMessage()));
    }

    public Mono<ContentPageResponse> getTopRatedTvs(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/top_rated")
                        .queryParam("api_key", apiKey)
                        .queryParam("page", page)
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching top rated tvs page {}: {}", page, error.getMessage()));
    }

    public Mono<ContentFullDetailResponse> getTvFullDetail(Long tvId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{tv_id}")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .queryParam("append_to_response", "credits,videos,watch/providers")
                        .build(tvId))
                .retrieve()
                .bodyToMono(ContentFullDetailResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching full tv detail for {}: {}", tvId, error.getMessage()));
    }

    public Mono<ContentPageResponse> discoverKoreanTVSeries(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/tv")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .queryParam("sort_by", "popularity.desc")
                        .queryParam("air_date.gte", "1998-01-01")
                        .queryParam("air_date.lte", "2025-08-15")
                        .queryParam("with_original_language", "ko")
                        .queryParam("vote_average.gte", "6")
                        .queryParam("vote_count.gte", "50")
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error discovering Korean TV series: {}", error.getMessage()));
    }

    public Mono<ContentPageResponse> discoverJapanTVSeries(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/tv")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .queryParam("sort_by", "first_air_date.desc")
                        .queryParam("air_date.gte", "1998-01-01")
                        .queryParam("air_date.lte", "2025-08-15")
                        .queryParam("with_original_language", "ja")
                        .queryParam("vote_average.gte", "6")
                        .queryParam("vote_count.gte", "300")
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error discovering Japan TV series: {}", error.getMessage()));
    }

    public Mono<ContentPageResponse> discoverPopularTVSeries(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/tv")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .queryParam("sort_by", "first_air_date.desc")
                        .queryParam("air_date.gte", "1998-01-01")
                        .queryParam("air_date.lte", "2025-08-15")
                        .queryParam("vote_average.gte", "6")
                        .queryParam("vote_count.gte", "350")
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error discovering Popular TV series: {}", error.getMessage()));
    }
}
