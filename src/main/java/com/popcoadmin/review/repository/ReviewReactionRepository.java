package com.popcoadmin.review.repository;

import com.popcoadmin.review.entity.Review;
import com.popcoadmin.review.entity.ReviewReaction;
import com.popcoadmin.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, Long> {
    Optional<ReviewReaction> findByReviewAndUser(Review review, User user);
    Integer countByReview(Review review);

    // 최근 7일간 좋아요가 추가된 리뷰별 좋아요 수 집계
    @Query("""
        SELECT r.review, COUNT(r) as likeCount
        FROM ReviewReaction r
        WHERE r.createdAt >= :startDate
        GROUP BY r.review
        ORDER BY COUNT(r) DESC
        """)
    List<Object[]> findTopReviewsByRecentLikes(@Param("startDate") LocalDateTime startDate);

    // 특정 리뷰의 최근 7일간 좋아요 수 조회
    @Query("""
        SELECT COUNT(r)
        FROM ReviewReaction r
        WHERE r.review = :review
        AND r.createdAt >= :startDate
        """)
    Integer countRecentLikesByReview(@Param("review") Review review, @Param("startDate") LocalDateTime startDate);
}
