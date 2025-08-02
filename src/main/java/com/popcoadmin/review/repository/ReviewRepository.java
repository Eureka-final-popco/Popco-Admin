package com.popcoadmin.review.repository;

import com.popcoadmin.content.entity.Content;
import com.popcoadmin.content.entity.key.ContentId;
import com.popcoadmin.review.dto.response.ReviewRatingDistributionDto;
import com.popcoadmin.review.entity.Review;
import com.popcoadmin.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT new com.popcoadmin.content.entity.key.ContentId(r.content.id.id, r.content.id.type) " +
            "FROM Review r " +
            "WHERE r.updatedAt BETWEEN :start AND :end " +
            "GROUP BY r.content.id.id, r.content.id.type")
    List<ContentId> findDistinctContentIdsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    Integer countByContentId(ContentId contentId);

    @Query("SELECT r FROM Review r WHERE r.content.id.id = :contentId AND r.content.id.type = :contentType " +
            "AND r.status <> 'BLIND' ORDER BY r.updatedAt DESC")
    List<Review> findByContentIdAndType(@Param("contentId") Long contentId, @Param("contentType") String contentType);

    List<Review> findByContentAndUpdatedAtBetween(Content content, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new com.popcoadmin.review.dto.response.ReviewRatingDistributionDto(CAST(ROUND(r.score) AS int), COUNT(r)) " +
            "FROM Review r WHERE r.content.id.id = :contentId AND r.content.id.type = :contentType " +
            "GROUP BY CAST(ROUND(r.score) AS int)" +
            "ORDER BY CAST(ROUND(r.score) AS int) DESC")
    List<ReviewRatingDistributionDto> findRatingDistribution(@Param("contentId") Long contentId,
                                                             @Param("contentType") String contentType);
}
