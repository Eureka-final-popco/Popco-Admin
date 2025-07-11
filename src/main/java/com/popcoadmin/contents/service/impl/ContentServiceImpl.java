package com.popcoadmin.contents.service.impl;

import com.popcoadmin.contents.component.TmdbApiClient;
import com.popcoadmin.contents.dto.response.MovieDto;
import com.popcoadmin.contents.dto.response.MovieListResponse;
import com.popcoadmin.contents.entity.Content;
import com.popcoadmin.contents.entity.ContentGenre;
import com.popcoadmin.contents.entity.Genre;
import com.popcoadmin.contents.repository.ContentGenreRepository;
import com.popcoadmin.contents.repository.ContentRepository;
import com.popcoadmin.contents.repository.GenreRepository;
import com.popcoadmin.contents.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {
    private final ContentRepository contentRepository;
    private final ContentGenreRepository contentGenreRepository;
    private final RestTemplate restTemplate;
    private final TmdbApiClient tmdbApiClient;
    private final GenreRepository genreRepository;

    @Value("${tmdb.api.key}")
    private String API_KEY;

//    public void saveGenresFromTmdb() {
//        String url = "https://api.themoviedb.org/3/genre/movie/list" + "?api_key=" + API_KEY + "&language=ko-KO";
//
//        GenresListResponse response = restTemplate.getForObject(url, GenresListResponse.class);
//
//        if (response != null && response.getGenres() != null) {
//            for (GenresDto dto : response.getGenres()) {
//                if (!genresRepository.existsById(dto.getId())) {
//                    Genres genre = Genres.toGenresEntity(dto);
//                    genresRepository.save(genre);
//                }
//            }
//        }
//    }

    public void fetchAndSavePage() {
        int page = 100;


        List<Genre> allGenres = genreRepository.findAll(); // 한 번에 다 가져옴
        Map<Long, Genre> genreCache = allGenres.stream()
                .collect(Collectors.toMap(Genre::getGenreId, g -> g));

        for (int i = 10; i < 50; i++) {
            String url =
                    "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=ko-KO&primary_release_date.gte=2001-01-01" +
                            "&primary_release_date.lte=2025-06-27&sort_by=popularity.desc&region=KR&with_original_language=en&vote_average.gte=6.5&certification_country=US" +
                            "&vote_count.gte=100.0&certification.lte=17&page=" + i + "&api_key=" + API_KEY;
            MovieListResponse response = restTemplate.getForObject(url, MovieListResponse.class);

            List<Content> movies = new ArrayList<>();
            List<ContentGenre> movieGenres = new ArrayList<>();

            if (response != null && response.getResults() != null) {

                for (MovieDto movieDto : response.getResults()) {
                    Content movie = Content.toMovieEntity(movieDto);
                    movies.add(movie);

                    for (Long genreId : movieDto.getGenreIds()) {
                        Genre genre = genreCache.get(genreId);
                        if (genre == null) {
                            throw new IllegalArgumentException("존재하지 않는 장르 ID: " + genreId);
                        }

                        MovieGenresKey key = new MovieGenresKey(movie.getMovieId(), genreId);
                        MovieGenres relation = new MovieGenres(key, movie, genre);
                        movieGenres.add(relation);
                    }
                }
            }

            saveAllMoviesAndGenres(movies, movieGenres);
        }
    }
}
