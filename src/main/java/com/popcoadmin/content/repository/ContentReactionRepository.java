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

import java.time.LocalDateTime;

@Repository
public interface ContentReactionRepository extends
        CrudRepository<ContentReaction, Long>, PagingAndSortingRepository<ContentReaction, Long> {
    @Query("SELECT new com.popcoadmin.content.dto.response.content.PopularContentStats(" +
            "cr.content, COUNT(cr.content.id.id) as likeCount) " +
            "FROM ContentReaction cr " +
            "WHERE cr.reaction = 'LIKE' " +
            "AND cr.updatedAt >= :startDateTime " +
            "AND cr.updatedAt < :endDateTime " +
            "AND (:type IS NULL OR cr.content.id.type = :type) " +
            "GROUP BY cr.content.id.id, cr.content.id.type, cr.content.releaseDate " +
            "ORDER BY COUNT(cr.content.id.id) DESC, cr.content.releaseDate DESC")
    Page<PopularContentStats> findPopularContentStatsByType(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("type") String type,
            Pageable pageable
    );


}
