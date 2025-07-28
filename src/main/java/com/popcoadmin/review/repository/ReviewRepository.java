package com.popcoadmin.review.repository;

import com.popcoadmin.content.entity.Content;
import com.popcoadmin.content.entity.key.ContentId;
import com.popcoadmin.review.entity.Review;
import com.popcoadmin.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Boolean existsReviewByContentAndUser(Content content, User user);

    // 3일 전 이후에 생성된 리뷰들의 content 정보를 가져오기 (복합키 사용)
    @Query("SELECT new com.popcoadmin.content.entity.key.ContentId(r.content.id.id, r.content.id.type) " +
            "FROM Review r " +
            "WHERE r.updatedAt >= :threeDaysAgo " +
            "GROUP BY r.content.id.id, r.content.id.type")
    List<ContentId> findDistinctContentIdsCreatedAfter(@Param("threeDaysAgo") LocalDateTime threeDaysAgo);

    Integer countByContentId(ContentId contentId);

    // 특정 콘텐츠의 모든 리뷰 조회
    @Query("SELECT r FROM Review r WHERE r.content.id.id = :contentId AND r.content.id.type = :contentType " +
            "AND r.status <> 'BLIND' ORDER BY r.updatedAt DESC")
    List<Review> findByContentIdAndType(@Param("contentId") Long contentId, @Param("contentType") String contentType);

}
