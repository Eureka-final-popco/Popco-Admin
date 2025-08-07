package com.popcoadmin.review.scheduler;

import com.popcoadmin.review.service.ReviewService;
import com.popcoadmin.review.service.ReviewSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewScheduler {

    private final ReviewService reviewService;
    private final ReviewSummaryService reviewSummaryService;

    /**
     * 매일 새벽 2시에 인기 리뷰 계산 실행
     * cron = "초 분 시 일 월 요일"
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void calculateTrendingReviews() {
        log.info("인기 리뷰 계산 스케줄러 시작");

        try {
            reviewService.calculateAndSaveTrendingReviews();
            log.info("인기 리뷰 계산 스케줄러 완료");
        } catch (Exception e) {
            log.error("인기 리뷰 계산 스케줄러 실행 중 오류", e);
        }
    }

    /**
     * 애플리케이션 시작 시 초기 데이터 생성
     * 테스트나 첫 배포 시 유용
     */
    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void initializeTrendingReviews() {
        log.info("초기 인기 리뷰 데이터 생성");

        try {
            reviewService.calculateAndSaveTrendingReviews();
            log.info("초기 인기 리뷰 데이터 생성 완료");
        } catch (Exception e) {
            log.error("초기 인기 리뷰 데이터 생성 중 오류", e);
        }
    }

    @Scheduled(cron = "0 0 1 */3 * *")
    public void scheduleReviewSummaryProcessing() {
        log.info("스케줄된 리뷰 요약 작업 시작");
        try {
            reviewSummaryService.processReviewSummaries();
        } catch (Exception e) {
            log.error("스케줄된 리뷰 요약 작업 중 오류 발생", e);
        }
    }
}
