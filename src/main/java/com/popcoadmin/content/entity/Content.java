package com.popcoadmin.content.entity;

import com.popcoadmin.content.dto.response.content.ContentResponse;
import com.popcoadmin.content.entity.key.ContentId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"casts", "crews", "videos", "watchProviders"})
public class Content {

    @EmbeddedId
    private ContentId id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "runtime")
    private Integer runtime;

    @Column(name = "rating_count")
    private Long ratingCount;

    @Column(name = "rating_average")
    private BigDecimal ratingAverage;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "backdrop_path")
    private String backdropPath;

    @ElementCollection
    @CollectionTable(
            name = "content_genres",
            joinColumns = {
                    @JoinColumn(name = "content_id",   referencedColumnName = "id"),
                    @JoinColumn(name = "content_type", referencedColumnName = "type")
            }
    )
    @Column(name = "genre_id")
    private Set<Integer> genreIds = new HashSet<>();

    // 관계 매핑
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CastMember> casts = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Crew> crews = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContentVideo> videos = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WatchProvider> watchProviders = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return Objects.equals(id, content.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static Content tvFrom(ContentResponse dto) {
        Content content = new Content();
        ContentId contentId = new ContentId(dto.getId(), "tv");
        content.setId(contentId);
        content.setTitle(dto.getName());
        content.setOverview(dto.getOverview());
        content.setReleaseDate(dto.getReleaseDate());
        content.setRatingCount(0L);
        content.setRatingAverage(BigDecimal.valueOf(0));
        content.setPosterPath(dto.getPosterPath());
        content.setBackdropPath(dto.getBackdropPath());

        if (dto.getGenreIds() != null) {
            content.setGenreIds(new HashSet<>(dto.getGenreIds()));
        }

        return content;
    }

    public static Content movieFrom(ContentResponse dto) {
        Content content = new Content();
        ContentId contentId = new ContentId(dto.getId(), "movie");
        content.setId(contentId);
        content.setTitle(dto.getTitle());
        content.setOverview(dto.getOverview());
        content.setReleaseDate(dto.getReleaseDate());
        content.setRatingCount(0L);
        content.setRatingAverage(BigDecimal.valueOf(0));
        content.setPosterPath(dto.getPosterPath());
        content.setBackdropPath(dto.getBackdropPath());

        if (dto.getGenreIds() != null) {
            content.setGenreIds(new HashSet<>(dto.getGenreIds()));
        }

        return content;
    }
}