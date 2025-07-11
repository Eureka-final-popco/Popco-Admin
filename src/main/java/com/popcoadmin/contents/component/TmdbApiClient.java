package com.popcoadmin.contents.component;

import com.popcoadmin.contents.dto.response.PopularListResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TmdbApiClient {
    
    @Value("${tmdb.api.key}")
    private String apiKey;
    
    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    public TmdbApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public PopularListResponseDto getMovieList() {
        String url = baseUrl + "?api_key=" + apiKey + "&language=ko-KR&region=kr&page=1";
        return restTemplate.getForObject(url, PopularListResponseDto.class);
    }
    
//    public TmdbMovieResponse getMovieWithVideos(Long tmdbId) {
//        String url = baseUrl + "/movie/" + tmdbId +
//                    "?api_key=" + apiKey + "&language=ko-KR&append_to_response=videos,credits";
//        return restTemplate.getForObject(url, TmdbMovieResponse.class);
//    }
}