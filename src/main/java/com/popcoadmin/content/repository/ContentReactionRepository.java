package com.popcoadmin.content.repository;

import com.popcoadmin.content.dto.response.content.PopularContentStats;
import com.popcoadmin.content.entity.ContentReaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContentReactionRepository extends
        CrudRepository<ContentReaction, Long>, PagingAndSortingRepository<ContentReaction, Long> {
    @Query("SELECT new com.popcoadmin.content.dto.response.content.PopularContentStats(" +
            "cr.content.id.id, cr.content.id.type, COUNT(cr.content.id.id) as likeCount) " +
            "FROM ContentReaction cr " +
            "WHERE cr.reaction = 'LIKE' " +
            "AND cr.createdAt >= :startDateTime " +
            "AND cr.createdAt < :endDateTime " +
            "GROUP BY cr.content.id.id, cr.content.id.type, cr.content.releaseDate " +
            "ORDER BY COUNT(cr.content.id.id) DESC, cr.content.releaseDate DESC")
    Page<PopularContentStats> findPopularContentStats(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable
    );
}
