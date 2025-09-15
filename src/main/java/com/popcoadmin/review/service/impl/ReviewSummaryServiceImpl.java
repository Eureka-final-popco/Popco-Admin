package com.popcoadmin.review.service.impl;

import com.popcoadmin.content.entity.Content;
import com.popcoadmin.content.entity.ContentGenre;
import com.popcoadmin.content.entity.key.ContentId;
import com.popcoadmin.content.repository.ContentGenreRepository;
import com.popcoadmin.content.repository.ContentRepository;
import com.popcoadmin.review.dto.response.ReviewRatingDistributionDto;
import com.popcoadmin.review.entity.ReviewSummary;
import com.popcoadmin.review.entity.Review;
import com.popcoadmin.review.gemini.LLMAnalysisService;
import com.popcoadmin.review.gemini.dto.LLMAnalysisRequest;
import com.popcoadmin.review.gemini.dto.LLMAnalysisResult;
import com.popcoadmin.review.gemini.dto.ReviewSummaryDto;
import com.popcoadmin.review.gemini.dto.enums.SummaryStrategyType;
import com.popcoadmin.review.repository.ReviewRepository;
import com.popcoadmin.review.repository.ReviewSummaryRepository;
import com.popcoadmin.review.service.ReviewSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewSummaryServiceImpl implements ReviewSummaryService {
    private final ReviewRepository reviewRepository;
    private final ContentRepository contentRepository;
    private final LLMAnalysisService llmService;
    private final ContentGenreRepository contentGenreRepository;
    private final ReviewSummaryRepository reviewSummaryRepository;

    public void processReviewSummaries() {
        log.info("리뷰 요약 작업 시작");

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(3);

        List<ContentId> recentContentIds = reviewRepository.findDistinctContentIdsBetween(start, end);

        for (ContentId contentId : recentContentIds) {
            try {
                processContentReviewSummary(contentId);
            } catch (Exception e) {
                log.error("콘텐츠 {}({}) 리뷰 요약 처리 중 오류 발생",
                        contentId.getId(), contentId.getType(), e);
            }
        }

        removeInsufficientReviewSummaries();

        log.info("리뷰 요약 작업 완료");
    }

    private void processContentReviewSummary(ContentId contentId) {

        Optional<Content> contentOpt = contentRepository.findById(contentId);
        if (contentOpt.isEmpty()) {
            log.warn("콘텐츠 {}({})를 찾을 수 없음", contentId.getId(), contentId.getType());
            return;
        }

        Content content = contentOpt.get();
        List<ContentGenre> genres = contentGenreRepository.findByContent(content);
        Integer reviewCount = reviewRepository.countByContentId(contentId);

        if (reviewCount >= 5) {
            log.info("콘텐츠 {}({})의 총 리뷰 {}개 - 요약 생성 진행",
                    contentId.getId(), contentId.getType(), reviewCount);

            Optional<ReviewSummary> existingSummary = reviewSummaryRepository.findByContent(content);

            if (existingSummary.isPresent()) {
                Long lastSummaryCount = existingSummary.get().getReviewCount();

                if (reviewCount > lastSummaryCount && (reviewCount / 5) > (lastSummaryCount / 5)) {
                    log.info("기존의 리뷰{}, 추가된 리뷰{} - 요약 생성 진행",
                            lastSummaryCount, reviewCount - lastSummaryCount);

                    LocalDateTime end = LocalDateTime.now();
                    LocalDateTime start = end.minusDays(3);

                    List<Review> recentReviews = reviewRepository.findByContentAndUpdatedAtBetween(content, start, end);

                    List<ReviewRatingDistributionDto> ratingHistogram = reviewRepository.findRatingDistributionBeforeDate(
                            content.getId().getId(), content.getId().getType(), start);

                    ReviewSummaryDto reviewSummary = ReviewSummaryDto.of(existingSummary.get(), ratingHistogram);
                    LLMAnalysisRequest analysisRequest = LLMAnalysisRequest.ofUpdate(recentReviews, content, genres, reviewSummary, SummaryStrategyType.UPDATE_PARTIAL);

                    LLMAnalysisResult analysisResult = llmService.analyzeReviews(analysisRequest);

                    updateExistingSummary(content, existingSummary.get(), analysisResult);
                }
            } else {
                List<Review> reviews = reviewRepository.findByContentIdAndType(
                        contentId.getId(), contentId.getType());

                LLMAnalysisRequest analysisRequest = LLMAnalysisRequest.ofInitial(content, genres, reviews, SummaryStrategyType.INITIAL);

                LLMAnalysisResult analysisResult = llmService.analyzeReviews(analysisRequest);
                createNewSummary(content, analysisResult);
            }
        } else {
            log.info("콘텐츠 {}({})의 리뷰 {}개 - 요약 생성 조건 미달",
                    contentId.getId(), contentId.getType(), reviewCount);
        }
    }

    private void updateExistingSummary(Content content, ReviewSummary summary, LLMAnalysisResult analysisResult) {
        Long reviewCount = reviewRepository.countByContent(content);
        BigDecimal reviewAvg = reviewRepository.findAverageScoreByContentIdAndType(content.getId().getId(), content.getId().getType());

        summary.updateSummary(
                analysisResult.getSummary(), analysisResult.getEvaluation(), reviewAvg, reviewCount);

        reviewSummaryRepository.save(summary);
        log.info("콘텐츠 {}({}) 요약 업데이트 완료",
                summary.getContent().getId().getId(),
                summary.getContent().getId().getType()
        );
    }

    private void createNewSummary(Content content, LLMAnalysisResult analysisResult) {
        Long reviewCount = reviewRepository.countByContent(content);
        BigDecimal reviewAvg = reviewRepository.findAverageScoreByContentIdAndType(content.getId().getId(), content.getId().getType());

        ReviewSummary newSummary =
                ReviewSummary.of(content, analysisResult.getSummary(), analysisResult.getEvaluation(), reviewAvg, reviewCount);

        reviewSummaryRepository.save(newSummary);
        log.info("콘텐츠 {}({}) 요약 생성 완료",
                content.getId().getId(), content.getId().getType());
    }

    private void removeInsufficientReviewSummaries() {
        log.info("리뷰 부족 콘텐츠 요약 제거 작업 시작");

        List<ReviewSummary> allSummaries = reviewSummaryRepository.findAll();

        for (ReviewSummary summary : allSummaries) {
            ContentId contentId = summary.getContent().getId();
            Integer currentReviewCount = reviewRepository.countByContentId(contentId);

            if (currentReviewCount <= 5) {
                reviewSummaryRepository.delete(summary);
                log.info("콘텐츠 {}({}) 요약 제거 - 현재 리뷰 {}개",
                        contentId.getId(), contentId.getType(), currentReviewCount);
            }
        }

        log.info("리뷰 부족 콘텐츠 요약 제거 작업 완료");
    }
}
