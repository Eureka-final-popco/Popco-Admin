package com.popcoadmin.review.repository;

import com.popcoadmin.review.entity.TrendingReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrendingReviewRepository extends JpaRepository<TrendingReview, Long> {

    // 기존 데이터 모두 삭제
    @Modifying
    @Query("DELETE FROM TrendingReview")
    void deleteAllTrendingReviews();
}
