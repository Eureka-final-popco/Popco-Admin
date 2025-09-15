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
public class TmdbMovieApiClient {

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
                        .path("/genre/movie/list")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .bodyToMono(GenreListResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching genres: {}", error.getMessage()));
    }

    // 모든 영화 Provider 목록 가져오기
    public Mono<ProviderListResponse> getAllMovieProviders() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/watch/providers/movie")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .queryParam("watch_region", "KR")
                        .build())
                .retrieve()
                .bodyToMono(ProviderListResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching movie providers: {}", error.getMessage()));
    }

    public Mono<ContentPageResponse> getNowPlayingMovies(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/now_playing")
                        .queryParam("api_key", apiKey)
                        .queryParam("page", page)
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching popular movies page {}: {}", page, error.getMessage()));
    }

    public Mono<ContentPageResponse> discoverKoreanMovies(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/movie")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .queryParam("region", "KR")
                        .queryParam("include_adult", false)
                        .queryParam("sort_by", "primary_release_date.desc")
                        .queryParam("primary_release_date.gte", "1998-01-01")
                        .queryParam("primary_release_date.lte", "2025-08-15")
                        .queryParam("with_original_language", "ko")
                        .queryParam("vote_average.gte", "6")
                        .queryParam("vote_count.gte", "3")
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error discovering Korean movies: {}", error.getMessage()));
    }


    public Mono<ContentPageResponse> discoverJapanMovies(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/movie")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .queryParam("region", "KR")
                        .queryParam("include_adult", false)
                        .queryParam("sort_by", "primary_release_date.desc")
                        .queryParam("primary_release_date.gte", "1998-01-01")
                        .queryParam("primary_release_date.lte", "2025-08-15")
                        .queryParam("with_original_language", "ja")
                        .queryParam("vote_average.gte", "7")
                        .queryParam("vote_count.gte", "350")
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error discovering Japan movies: {}", error.getMessage()));
    }

    public Mono<ContentPageResponse> discoverPopularMovies(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/movie")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .queryParam("region", "KR")
                        .queryParam("include_adult", false)
                        .queryParam("sort_by", "primary_release_date.desc")
                        .queryParam("primary_release_date.gte", "1998-01-01")
                        .queryParam("primary_release_date.lte", "2025-08-15")
                        .queryParam("vote_average.gte", "7")
                        .queryParam("vote_count.gte", "350")
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error discovering Popular movies: {}", error.getMessage()));
    }


    public Mono<ContentPageResponse> getPopularMovies(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/popular")
                        .queryParam("api_key", apiKey)
                        .queryParam("page", page)
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching popular movies page {}: {}", page, error.getMessage()));
    }

    public Mono<ContentPageResponse> getTopRatedMovies(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/top_rated")
                        .queryParam("api_key", apiKey)
                        .queryParam("page", page)
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching top rated movies page {}: {}", page, error.getMessage()));
    }

    public Mono<ContentPageResponse> getUpcomingMovies(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/upcoming")
                        .queryParam("api_key", apiKey)
                        .queryParam("page", page)
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .bodyToMono(ContentPageResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching upcoming movies page {}: {}", page, error.getMessage()));
    }

    public Mono<ContentFullDetailResponse> getMovieFullDetail(Long movieId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{movie_id}")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .queryParam("append_to_response", "credits,videos,watch/providers")
                        .build(movieId))
                .retrieve()
                .bodyToMono(ContentFullDetailResponse.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelay)))
                .doOnError(error -> log.error("Error fetching full movie detail for {}: {}", movieId, error.getMessage()));
    }
}
