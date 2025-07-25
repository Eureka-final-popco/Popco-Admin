package com.popcoadmin.review.service.impl;

import com.popcoadmin.review.entity.Review;
import com.popcoadmin.review.entity.TrendingReview;
import com.popcoadmin.review.entity.enums.ReviewStatus;
import com.popcoadmin.review.repository.ReviewReactionRepository;
import com.popcoadmin.review.repository.TrendingReviewRepository;
import com.popcoadmin.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewReactionRepository reviewReactionRepository;
    private final TrendingReviewRepository trendingReviewRepository;

    private static final int TRENDING_REVIEW_LIMIT = 50; // 상위 50개 리뷰 저장

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void calculateAndSaveTrendingReviews() {
        log.info("인기 리뷰 계산 시작");

        try {
            // 1. 기존 데이터 삭제
            trendingReviewRepository.deleteAllTrendingReviews();

            // 2. 최근 7일 기준 시점
            LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

            // 3. 최근 7일간 좋아요가 많은 리뷰 조회
            List<Object[]> results = reviewReactionRepository.findTopReviewsByRecentLikes(oneWeekAgo);

            // 4. TrendingReview 엔티티로 변환 및 저장
            List<TrendingReview> trendingReviews = new ArrayList<>();
            int ranking = 1;

            for (Object[] result : results) {
                if (ranking > TRENDING_REVIEW_LIMIT) break;

                Review review = (Review) result[0];
                Long likeCount = (Long) result[1];

                // 블라인드 처리된 리뷰는 제외
                if (review.getStatus() == ReviewStatus.BLIND) {
                    continue;
                }

                TrendingReview trendingReview = TrendingReview.of(
                        review,
                        likeCount.intValue(),
                        ranking++
                );

                trendingReviews.add(trendingReview);
            }

            trendingReviewRepository.saveAll(trendingReviews);
            log.info("인기 리뷰 계산 완료. 저장된 리뷰 수: {}", trendingReviews.size());

        } catch (Exception e) {
            log.error("인기 리뷰 계산 중 오류 발생", e);
            throw e;
        }
    }
}
