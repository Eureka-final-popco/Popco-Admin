package com.popcoadmin.content.service.impl;

import com.popcoadmin.apiclient.TmdbMovieApiClient;
import com.popcoadmin.apiclient.TmdbTvApiClient;
import com.popcoadmin.content.dto.response.content.ContentDetailResponse;
import com.popcoadmin.content.dto.response.content.ContentFullDetailResponse;
import com.popcoadmin.content.dto.response.content.ContentPageResponse;
import com.popcoadmin.content.dto.response.credit.CastResponse;
import com.popcoadmin.content.dto.response.credit.CreditsResponse;
import com.popcoadmin.content.dto.response.credit.CrewResponse;
import com.popcoadmin.content.dto.response.videos.VideoResponse;
import com.popcoadmin.content.dto.response.videos.VideosResponse;
import com.popcoadmin.content.dto.response.watchprovider.WatchProviderCountry;
import com.popcoadmin.content.dto.response.watchprovider.WatchProviderInfo;
import com.popcoadmin.content.dto.response.watchprovider.WatchProvidersResponse;
import com.popcoadmin.content.entity.*;
import com.popcoadmin.content.entity.key.ContentId;
import com.popcoadmin.content.repository.*;
import com.popcoadmin.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentServiceImpl implements ContentService {

    private final TmdbTvApiClient tmdbTvApiClient;
    private final TmdbMovieApiClient tmdbMovieApiClient;

    private final ContentRepository contentRepository;
    private final GenreRepository genreRepository;
    private final ProviderRepository providerRepository;
    private final CastRepository castRepository;
    private final CrewRepository crewRepository;
    private final ActorRepository actorRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final ContentVideoRepository videoRepository;
    private final WatchProviderRepository watchProviderRepository;

    @Override
    @Transactional
    public void syncAllContentData(int pagesPerCategory, boolean includeDetails) {
        // 1. 장르 동기화
        syncGenres();

        // 2. Provider 동기화
        syncAllProviders();

        // 3. 기본 데이터 동기화
        discoverKoreanMovies(pagesPerCategory);
        discoverKoreanTVSeries(pagesPerCategory);
        discoverJapanMovies(pagesPerCategory);
        discoverJapanTVSeries(pagesPerCategory);
        discoverPopularMovies(pagesPerCategory);
        discoverPopularTVSeries(pagesPerCategory);
//        syncNowPlayingMovies(pagesPerCategory);
//        syncUpcomingMovies(pagesPerCategory);
//        syncOnTheAirTvs(pagesPerCategory);
//        syncPopular(pagesPerCategory);
//        syncTopRated(pagesPerCategory);

        if (includeDetails) {
            // 4. 통합 상세 정보 동기화 (한 번의 API 호출로 모든 정보 가져오기)
            List<Long> allMovieIds = contentRepository.findAllMovieIds();
            List<Long> allTvIds = contentRepository.findAllTvIds();

            log.info("Found {} movies and {} tvs. Starting full details sync...",
                    allMovieIds.size(), allTvIds.size());

            // 배치로 처리
            int batchSize = 20;

//            // 영화 상세 정보 (한 번의 호출로 모든 정보)
//            for (int i = 0; i < allMovieIds.size(); i += batchSize) {
//                List<Long> batch = allMovieIds.subList(i, Math.min(i + batchSize, allMovieIds.size()));
//                log.info("Processing movie batch {}/{}", (i/batchSize) + 1, (allMovieIds.size()/batchSize) + 1);
//                syncMovieFullDetails(batch);
//            }

            // TV 상세 정보 (한 번의 호출로 모든 정보)
            for (int i = 0; i < allTvIds.size(); i += batchSize) {
                List<Long> batch = allTvIds.subList(i, Math.min(i + batchSize, allTvIds.size()));
                log.info("Processing tv batch {}/{}", (i/batchSize) + 1, (allTvIds.size()/batchSize) + 1);
                syncTvFullDetails(batch);
            }
        }

        log.info("All content data synchronization completed!");
    }

    @Transactional
    public void syncGenres() {
        log.info("Starting genre synchronization...");

        tmdbMovieApiClient.getGenres()
                .doOnNext(response -> log.info("Fetched {} Movie genres", response.getGenres().size()))
                .flatMapMany(response -> Flux.fromIterable(response.getGenres()))
                .map(Genre::from)
                .collectList()
                .doOnNext(genres -> {
                    genreRepository.saveAll(genres);
                    log.info("Saved {} Movie genres to database", genres.size());
                })
                .block();

        tmdbTvApiClient.getGenres()
                .doOnNext(response -> log.info("Fetched {} TV genres", response.getGenres().size()))
                .flatMapMany(response -> Flux.fromIterable(response.getGenres()))
                .map(Genre::from)
                .collectList()
                .doOnNext(genres -> {
                    genreRepository.saveAll(genres);
                    log.info("Saved {} TV genres to database", genres.size());
                })
                .block();
    }

    @Transactional
    public void syncAllProviders() {
        log.info("Starting synchronization of all available providers...");

        // 영화 Provider 동기화
        tmdbMovieApiClient.getAllMovieProviders()
                .doOnNext(response -> log.info("Fetched {} movie providers", response.getResults().size()))
                .flatMapMany(response -> Flux.fromIterable(response.getResults()))
                .map(Provider::from)
                .collectList()
                .doOnNext(providers -> {
                    providers.forEach(provider -> {
                        // 이미 존재하는지 확인 후 저장
                        if (!providerRepository.existsById(provider.getId())) {
                            providerRepository.save(provider);
                        }
                    });
                    log.info("Processed {} movie providers", providers.size());
                })
                .block();

        // TV Provider 동기화 (영화와 다른 provider가 있을 수 있음)
        tmdbTvApiClient.getAllTvProviders()
                .doOnNext(response -> log.info("Fetched {} TV providers", response.getResults().size()))
                .flatMapMany(response -> Flux.fromIterable(response.getResults()))
                .map(Provider::from)
                .collectList()
                .doOnNext(providers -> {
                    providers.forEach(provider -> {
                        // 이미 존재하는지 확인 후 저장 또는 업데이트
                        providerRepository.findById(provider.getId())
                                .ifPresentOrElse(
                                        existing -> {
                                            // 기존 provider 정보 업데이트 (필요한 경우)
                                            if (existing.getName() == null || existing.getName().isEmpty()) {
                                                existing.setName(provider.getName());
                                                existing.setLogoPath(provider.getLogoPath());
                                                providerRepository.save(existing);
                                            }
                                        },
                                        () -> providerRepository.save(provider)
                                );
                    });
                    log.info("Processed {} TV providers", providers.size());
                })
                .block();
    }

    @Transactional
    public void discoverKoreanMovies(int maxPages) {
        log.info("Starting now playing movies synchronization for {} pages...", maxPages);
        syncMovies(tmdbMovieApiClient::discoverKoreanMovies, maxPages, "now playing");
    }

    @Transactional
    public void discoverKoreanTVSeries(int maxPages) {
        log.info("Starting now playing movies synchronization for {} pages...", maxPages);
        syncTvs(tmdbTvApiClient::discoverKoreanTVSeries, maxPages, "now playing");
    }

    @Transactional
    public void discoverJapanMovies(int maxPages) {
        log.info("Starting now playing movies synchronization for {} pages...", maxPages);
        syncMovies(tmdbMovieApiClient::discoverJapanMovies, maxPages, "now playing");
    }

    @Transactional
    public void discoverJapanTVSeries(int maxPages) {
        log.info("Starting now playing movies synchronization for {} pages...", maxPages);
        syncTvs(tmdbTvApiClient::discoverJapanTVSeries, maxPages, "now playing");
    }

    @Transactional
    public void discoverPopularMovies(int maxPages) {
        log.info("Starting now playing movies synchronization for {} pages...", maxPages);
        syncMovies(tmdbMovieApiClient::discoverPopularMovies, maxPages, "now playing");
    }

    @Transactional
    public void discoverPopularTVSeries(int maxPages) {
        log.info("Starting now playing movies synchronization for {} pages...", maxPages);
        syncTvs(tmdbTvApiClient::discoverPopularTVSeries, maxPages, "now playing");
    }

    //    @Transactional
//    public void syncNowPlayingMovies(int maxPages) {
//        log.info("Starting now playing movies synchronization for {} pages...", maxPages);
//        syncMovies(tmdbMovieApiClient::getNowPlayingMovies, maxPages, "now playing");
//    }
//
//    @Transactional
//    public void syncUpcomingMovies(int maxPages) {
//        log.info("Starting upcoming movies synchronization for {} pages...", maxPages);
//        syncMovies(tmdbMovieApiClient::getUpcomingMovies, maxPages, "upcoming");
//    }
//
//    @Transactional
//    public void syncPopular(int maxPages) {
//        log.info("Starting popular movies synchronization for {} pages...", maxPages);
//        syncMovies(tmdbMovieApiClient::getPopularMovies, maxPages, "popular");
//
//        log.info("Starting popular tvs synchronization for {} pages...", maxPages);
//        syncTvs(tmdbTvApiClient::getPopularTvs, maxPages, "popular");
//    }
//
//    @Transactional
//    public void syncTopRated(int maxPages) {
//        log.info("Starting top-rated movies synchronization for {} pages...", maxPages);
//        syncMovies(tmdbMovieApiClient::getTopRatedMovies, maxPages, "top-rated");
//
//        log.info("Starting top-rated tvs synchronization for {} pages...", maxPages);
//        syncTvs(tmdbTvApiClient::getTopRatedTvs, maxPages, "top-rated");
//    }

    @Transactional
    public void syncMovieFullDetails(List<Long> movieIds) {
        log.info("Starting full details sync for {} movies", movieIds.size());

        // Flux 대신 일반 반복문 사용
        for (Long movieId : movieIds) {
            try {
                log.info("Fetching full details for movie ID: {}", movieId);
                ContentFullDetailResponse fullDetail = tmdbMovieApiClient.getMovieFullDetail(movieId)
                        .block(); // 각각을 동기적으로 처리

                if (fullDetail != null) {
                    processMovieFullDetail(fullDetail);
                    log.info("Processed full details for movie: {}", fullDetail.getTitle());
                }

                Thread.sleep(250); // Rate limiting
            } catch (Exception error) {
                log.error("Error fetching full details for movie {}: {}", movieId, error.getMessage());
            }
        }
    }

    @Transactional
    public void syncTvFullDetails(List<Long> tvIds) {
        log.info("Starting full details sync for {} tvs", tvIds.size());

        // Flux 대신 일반 반복문 사용
        for (Long tvId : tvIds) {
            try {
                log.info("Fetching full details for tv ID: {}", tvId);
                ContentFullDetailResponse fullDetail = tmdbTvApiClient.getTvFullDetail(tvId)
                        .block(); // 각각을 동기적으로 처리

                if (fullDetail != null) {
                    processTvFullDetail(fullDetail);
                    log.info("Processed full details for tv: {}", fullDetail.getName());
                }

                Thread.sleep(250); // Rate limiting
            } catch (Exception error) {
                log.error("Error fetching full details for tv {}: {}", tvId, error.getMessage());
            }
        }
    }

    // 통합 처리 메서드
    private void processMovieFullDetail(ContentFullDetailResponse fullDetail) {
        // 1. 기본 상세 정보 업데이트
        updateContentWithDetails(fullDetail, "movie");

        // 2. Credits 저장
        if (fullDetail.getCredits() != null) {
            fullDetail.getCredits().setId(fullDetail.getId()); // ID 설정
            saveCredits(fullDetail.getCredits(), "movie");
        }

        // 4. Videos 저장
        if (fullDetail.getVideos() != null) {
            fullDetail.getVideos().setId(fullDetail.getId());
            saveVideos(fullDetail.getVideos(), "movie");
        }

        // 5. Watch Providers 저장
        if (fullDetail.getWatchProviders() != null) {
            fullDetail.getWatchProviders().setId(fullDetail.getId());
            saveWatchProviders(fullDetail.getWatchProviders(), "movie");
        }
    }

    private void processTvFullDetail(ContentFullDetailResponse fullDetail) {
        // 1. 기본 상세 정보 업데이트
        updateContentWithDetails(fullDetail, "tv");

        // 2. Credits 저장
        if (fullDetail.getCredits() != null) {
            fullDetail.getCredits().setId(fullDetail.getId());
            saveCredits(fullDetail.getCredits(), "tv");
        }

        // 4. Videos 저장
        if (fullDetail.getVideos() != null) {
            fullDetail.getVideos().setId(fullDetail.getId());
            saveVideos(fullDetail.getVideos(), "tv");
        }

        // 5. Watch Providers 저장
        if (fullDetail.getWatchProviders() != null) {
            fullDetail.getWatchProviders().setId(fullDetail.getId());
            saveWatchProviders(fullDetail.getWatchProviders(), "tv");
        }
    }

    private void syncMovies(java.util.function.Function<Integer, Mono<ContentPageResponse>> apiCall, int maxPages, String type) {
        List<Content> allMovies = new ArrayList<>();

        Flux.range(1, maxPages)
                .concatMap(page -> {
                    log.info("Fetching {} movies page {}/{}", type, page, maxPages);
                    return apiCall.apply(page)
                            .delayElement(Duration.ofMillis(250)); // API rate limiting
                })
                .doOnNext(response -> {
                    log.info("Received {} movies from page {}",
                            response.getResults().size(), response.getPage());
                })
                .flatMapIterable(ContentPageResponse::getResults)
                .map(Content::movieFrom)
                .buffer(100) // Batch processing
                .doOnNext(movies -> {
                    contentRepository.saveAll(movies);
                    allMovies.addAll(movies);
                    log.info("Saved batch of {} movies, total: {}", movies.size(), allMovies.size());
                })
                .doOnComplete(() -> log.info("Completed {} movies sync. Total movies saved: {}",
                        type, allMovies.size()))
                .doOnError(error -> log.error("Error during {} movies sync: {}", type, error.getMessage()))
                .blockLast();
    }

    private void syncTvs(java.util.function.Function<Integer, Mono<ContentPageResponse>> apiCall, int maxPages, String type) {
        List<Content> allMovies = new ArrayList<>();

        Flux.range(1, maxPages)
                .concatMap(page -> {
                    log.info("Fetching {} tvs page {}/{}", type, page, maxPages);
                    return apiCall.apply(page)
                            .delayElement(Duration.ofMillis(250)); // API rate limiting
                })
                .doOnNext(response -> {
                    log.info("Received {} tvs from page {}",
                            response.getResults().size(), response.getPage());
                })
                .flatMapIterable(ContentPageResponse::getResults)
                .map(Content::tvFrom)
                .buffer(100) // Batch processing
                .doOnNext(tvs -> {
                    contentRepository.saveAll(tvs);
                    allMovies.addAll(tvs);
                    log.info("Saved batch of {} tvs, total: {}", tvs.size(), allMovies.size());
                })
                .doOnComplete(() -> log.info("Completed {} tvs sync. Total tvs saved: {}",
                        type, allMovies.size()))
                .doOnError(error -> log.error("Error during {} tvs sync: {}", type, error.getMessage()))
                .blockLast();
    }

    @Transactional
    public void updateContentWithDetails(ContentDetailResponse detail, String type) {
        ContentId contentId = new ContentId(detail.getId(), type);

        log.info("=== Updating content details ===");
        log.info("Content ID: {}, Type: {}", detail.getId(), type);

        contentRepository.findById(contentId).ifPresent(content -> {
            log.info("Found content: {}", content.getTitle());

            if ("tv".equals(type)) {
                log.info("Last air date from API: {}", detail.getLastAirDate());
                log.info("Current release_date in DB: {}", content.getReleaseDate());

                if (detail.getLastAirDate() != null) {
                    content.setReleaseDate(detail.getLastAirDate());
                    log.info("✅ Setting release_date to: {}", detail.getLastAirDate());
                } else {
                    log.warn("⚠️ Last air date is NULL for TV: {}", detail.getId());
                }
            }

            if (detail.getRuntime() != null) {
                content.setRuntime(detail.getRuntime());
                log.info("✅ Setting runtime to: {}", detail.getRuntime());
            } else {
                log.warn("⚠️ Runtime is NULL for content: {}", detail.getId());
            }

            Content savedContent = contentRepository.save(content);
            log.info("💾 Saved content - release_date: {}, runtime: {}",
                    savedContent.getReleaseDate(), savedContent.getRuntime());
            log.info("=== Update completed ===");
        });
    }

    @Transactional
    public void saveCredits(CreditsResponse credits, String type) {
        ContentId contentId = new ContentId(credits.getId(), type);

        Content content = contentRepository.findById(contentId).orElse(null);
        if (content == null) return;

        // 기존 데이터 삭제
        castRepository.deleteByContent_id(contentId);
        crewRepository.deleteByContent_Id(contentId);

        // 1. Actor ID들을 미리 수집하고 배치로 조회
        Set<Long> actorIds = credits.getCast().stream()
                .limit(20)
                .map(CastResponse::getId)
                .collect(Collectors.toSet());

        Map<Long, Actor> existingActors = actorRepository.findAllById(actorIds)
                .stream()
                .collect(Collectors.toMap(Actor::getId, actor -> actor));

        // 2. 존재하지 않는 Actor들을 배치로 생성
        List<Actor> newActors = credits.getCast().stream()
                .limit(20)
                .filter(castDto -> !existingActors.containsKey(castDto.getId()))
                .map(castDto -> {
                    Actor actor = new Actor();
                    actor.setId(castDto.getId());
                    actor.setName(castDto.getName());
                    actor.setProfilePath(castDto.getProfilePath());
                    actor.setGender(castDto.getGender());
                    return actor;
                })
                .collect(Collectors.toList());

        // 배치로 새로운 Actor들 저장 (중복 처리)
        if (!newActors.isEmpty()) {
            try {
                List<Actor> savedActors = actorRepository.saveAll(newActors);
                savedActors.forEach(actor -> existingActors.put(actor.getId(), actor));
            } catch (DataIntegrityViolationException e) {
                // 중복 키 예외 발생 시 개별적으로 처리
                for (Actor actor : newActors) {
                    try {
                        Actor savedActor = actorRepository.save(actor);
                        existingActors.put(savedActor.getId(), savedActor);
                    } catch (DataIntegrityViolationException ignored) {
                        // 이미 존재하는 경우 다시 조회
                        actorRepository.findById(actor.getId())
                                .ifPresent(existing -> existingActors.put(existing.getId(), existing));
                    }
                }
            }
        }

        // 3. CastMember 저장
        List<CastMember> castMembers = credits.getCast().stream()
                .limit(20)
                .map(castDto -> {
                    Actor actor = existingActors.get(castDto.getId());

                    CastMember castMember = new CastMember();
                    castMember.setActor(actor);
                    castMember.setContent(content);

                    String character = castDto.getCharacter();
                    if (character != null && character.length() > 1000) {
                        character = character.substring(0, 997) + "...";
                    }
                    castMember.setCharacter(character);
                    castMember.setOrder(castDto.getOrder());
                    return castMember;
                })
                .collect(Collectors.toList());
        castRepository.saveAll(castMembers);

        // 4. CrewMember도 동일한 방식으로 처리
        Set<Long> crewMemberIds = credits.getCrew().stream()
                .filter(crew -> Arrays.asList("Director", "Producer", "Writer").contains(crew.getJob()))
                .map(CrewResponse::getId)
                .collect(Collectors.toSet());

        Map<Long, CrewMember> existingCrewMembers = crewMemberRepository.findAllById(crewMemberIds)
                .stream()
                .collect(Collectors.toMap(CrewMember::getId, crewMember -> crewMember));

        List<CrewMember> newCrewMembers = credits.getCrew().stream()
                .filter(crew -> Arrays.asList("Director", "Producer", "Writer").contains(crew.getJob()))
                .filter(crewDto -> !existingCrewMembers.containsKey(crewDto.getId()))
                .map(crewDto -> {
                    CrewMember crewMember = new CrewMember();
                    crewMember.setId(crewDto.getId());
                    crewMember.setName(crewDto.getName());
                    crewMember.setProfilePath(crewDto.getProfilePath());
                    crewMember.setGender(crewDto.getGender());
                    crewMember.setKnownForDepartment(crewDto.getDepartment());
                    return crewMember;
                })
                .collect(Collectors.toList());

        if (!newCrewMembers.isEmpty()) {
            try {
                List<CrewMember> savedCrewMembers = crewMemberRepository.saveAll(newCrewMembers);
                savedCrewMembers.forEach(cm -> existingCrewMembers.put(cm.getId(), cm));
            } catch (DataIntegrityViolationException e) {
                for (CrewMember crewMember : newCrewMembers) {
                    try {
                        CrewMember saved = crewMemberRepository.save(crewMember);
                        existingCrewMembers.put(saved.getId(), saved);
                    } catch (DataIntegrityViolationException ignored) {
                        crewMemberRepository.findById(crewMember.getId())
                                .ifPresent(existing -> existingCrewMembers.put(existing.getId(), existing));
                    }
                }
            }
        }

        // 5. Crew 저장
        List<Crew> crews = credits.getCrew().stream()
                .filter(crew -> Arrays.asList("Director", "Producer", "Writer").contains(crew.getJob()))
                .map(crewDto -> {
                    CrewMember crewMember = existingCrewMembers.get(crewDto.getId());

                    Crew crew = new Crew();
                    crew.setCrewMember(crewMember);
                    crew.setContent(content);
                    crew.setJob(crewDto.getJob());
                    return crew;
                })
                .collect(Collectors.toList());
        crewRepository.saveAll(crews);
    }

    private void saveVideos(VideosResponse videos, String type) {
        ContentId contentId = new ContentId(videos.getId(), type);

        Content content = contentRepository.findById(contentId).orElse(null);
        if (content == null) return;

        videoRepository.deleteByContent_Id(contentId);

        List<ContentVideo> contentVideos = videos.getResults().stream()
                .filter(v -> "YouTube".equals(v.getSite()))
                .filter(VideoResponse::getOfficial)
                .limit(10) // 상위 10개만
                .map(video -> {
                    ContentVideo contentVideo = new ContentVideo();
                    contentVideo.setId(video.getId());
                    contentVideo.setContent(content);
                    contentVideo.setName(video.getName());
                    contentVideo.setKey(video.getKey());
                    contentVideo.setType(video.getType());
                    contentVideo.setOfficial(video.getOfficial());
                    return contentVideo;
                })
                .collect(Collectors.toList());

        videoRepository.saveAll(contentVideos);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected void saveWatchProviders(WatchProvidersResponse providers, String type) {
        ContentId contentId = new ContentId(providers.getId(), type);

        Content content = contentRepository.findById(contentId).orElse(null);
        if (content == null) return;

        watchProviderRepository.deleteByContent_Id(contentId);

        // HashSet 대신 ArrayList 사용하여 hashCode 호출 방지
        List<WatchProvider> watchProviders = new ArrayList<>();
        Set<Integer> processedProviderIds = new HashSet<>(); // 중복 체크용

        // 한국 제공자
        if (providers.getResults() != null && providers.getResults().getKr() != null) {
            WatchProviderCountry kr = providers.getResults().getKr();

            // 모든 타입의 provider를 하나로 통합
            List<WatchProviderInfo> allProviderInfos = new ArrayList<>();

            // 스트리밍
            if (kr.getFlatrate() != null) {
                allProviderInfos.addAll(kr.getFlatrate());
            }

            // 대여
            if (kr.getRent() != null) {
                allProviderInfos.addAll(kr.getRent());
            }

            // 구매
            if (kr.getBuy() != null) {
                allProviderInfos.addAll(kr.getBuy());
            }

            // 중복 제거하며 WatchProvider 생성
            for (WatchProviderInfo info : allProviderInfos) {
                if (!processedProviderIds.contains(info.getProviderId())) {
                    processedProviderIds.add(info.getProviderId());
                    WatchProvider watchProvider = createSimpleWatchProvider(content, info, "KR");
                    if (watchProvider != null) {
                        watchProviders.add(watchProvider);
                    }
                }
            }
        }

        watchProviderRepository.saveAll(watchProviders);
    }

    private WatchProvider createSimpleWatchProvider(Content content, WatchProviderInfo info, String country) {
        // Provider 조회 또는 생성
        Provider provider = providerRepository.findById(info.getProviderId())
                .orElseGet(() -> {
                    Provider newProvider = new Provider();
                    newProvider.setId(info.getProviderId());
                    newProvider.setName(info.getProviderName());
                    newProvider.setLogoPath(info.getLogoPath());
                    return providerRepository.save(newProvider);
                });

        WatchProvider watchProvider = new WatchProvider();
        watchProvider.setProvider(provider);
        watchProvider.setContent(content);

        return watchProvider;
    }

}
