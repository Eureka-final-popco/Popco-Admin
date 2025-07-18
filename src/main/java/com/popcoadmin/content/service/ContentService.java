package com.popcoadmin.content.service;

import java.util.List;

public interface ContentService {

    void syncAllContentData(int pagesPerCategory, boolean includeDetails);

    void syncGenres();

    void syncAllProviders();

    void syncNowPlayingMovies(int maxPages);

    void syncUpcomingMovies(int maxPages);

    void syncOnTheAirTvs(int maxPages);

    void syncPopular(int maxPages);

    void syncTopRated(int maxPages);

    void syncMovieFullDetails(List<Long> movieIds);

    void syncTvFullDetails(List<Long> tvIds);

}
