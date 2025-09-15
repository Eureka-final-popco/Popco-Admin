package com.popcoadmin.content.service;

import java.util.List;

public interface ContentService {

    void syncAllContentData(int pagesPerCategory, boolean includeDetails);

    void syncGenres();

    void syncAllProviders();

//    void discoverKoreanMovies(int maxPages);
//
    List<Long> discoverKoreanTVSeries(int maxPages);

//    void discoverKoreanTVSeries(int maxPages);
//
//    void discoverJapanMovies(int maxPages);
//
//    void discoverJapanTVSeries(int maxPages);
//
//    void discoverPopularMovies(int maxPages);
//
//    void discoverPopularTVSeries(int maxPages);

//    void syncNowPlayingMovies(int maxPages);
//
//    void syncUpcomingMovies(int maxPages);
//
//    void syncOnTheAirTvs(int maxPages);
//
//    void syncPopular(int maxPages);
//
//    void syncTopRated(int maxPages);

    void syncMovieFullDetails(List<Long> movieIds);

    void syncTvFullDetails(List<Long> tvIds);

}
