package com.popcoadmin.review.service.impl;

import com.popcoadmin.content.entity.Content;
import com.popcoadmin.content.entity.ContentGenre;
import com.popcoadmin.content.entity.key.ContentId;
import com.popcoadmin.content.repository.ContentGenreRepository;
import com.popcoadmin.content.repository.ContentRepository;
import com.popcoadmin.review.entity.ReviewSummary;
import com.popcoadmin.review.entity.Review;
import com.popcoadmin.review.gemini.LLMAnalysisService;
import com.popcoadmin.review.gemini.dto.LLMAnalysisResult;
import com.popcoadmin.review.repository.ReviewRepository;
import com.popcoadmin.review.repository.ReviewSummaryRepository;
import com.popcoadmin.review.service.ReviewSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewSummaryServiceImpl implements ReviewSummaryService {
    private final ReviewRepository reviewRepository;
    private final ReviewSummaryRepository summaryRepository;
    private final ContentRepository contentRepository;
    private final LLMAnalysisService llmService;
    private final ContentGenreRepository contentGenreRepository;

    public void processReviewSummaries() {
        log.info("리뷰 요약 작업 시작");

        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        // 3일 전 이후에 추가된 리뷰의 content 정보 추출 (복합키 사용)
        List<ContentId> contentIds = reviewRepository.findDistinctContentIdsCreatedAfter(threeDaysAgo);

        for (ContentId contentId : contentIds) {
            try {
                processContentReviewSummary(contentId);
            } catch (Exception e) {
                log.error("콘텐츠 {}({}) 리뷰 요약 처리 중 오류 발생",
                        contentId.getId(), contentId.getType(), e);
            }
        }

        // 리뷰가 5개 이하인 콘텐츠의 요약 제거
        removeInsufficientReviewSummaries();

        log.info("리뷰 요약 작업 완료");
    }

    /**
     * 특정 콘텐츠의 리뷰 요약 처리
     */
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
            log.info("콘텐츠 {}({})의 리뷰 {}개 - 요약 생성 진행",
                    contentId.getId(), contentId.getType(), reviewCount);

            // 리뷰 목록 조회
            List<Review> reviews = reviewRepository.findByContentIdAndType(
                    contentId.getId(), contentId.getType());

            // LLM을 통한 리뷰 분석
            LLMAnalysisResult analysisResult = llmService.analyzeReviews(reviews, content, genres);

            // 기존 요약이 있으면 업데이트, 없으면 생성
            Optional<ReviewSummary> existingSummary = summaryRepository.findByContent(content);

            if (existingSummary.isPresent()) {
                updateExistingSummary(existingSummary.get(), analysisResult);
            } else {
                createNewSummary(content, analysisResult);
            }
        } else {
            log.info("콘텐츠 {}({})의 리뷰 {}개 - 요약 생성 조건 미달",
                    contentId.getId(), contentId.getType(), reviewCount);
        }
    }

    /**
     * 기존 요약 업데이트
     */
    private void updateExistingSummary(ReviewSummary summary, LLMAnalysisResult analysisResult) {
        summary.updateSummary(
                analysisResult.getSummary(), analysisResult.getEvaluation());

        summaryRepository.save(summary);
        log.info("콘텐츠 {}({}) 요약 업데이트 완료",
                summary.getContent().getId().getId(),
                summary.getContent().getId().getType()
        );
    }

    /**
     * 새 요약 생성
     */
    private void createNewSummary(Content content, LLMAnalysisResult analysisResult) {
        ReviewSummary newSummary =
                ReviewSummary.of(content, analysisResult.getSummary(), analysisResult.getEvaluation());

        summaryRepository.save(newSummary);
        log.info("콘텐츠 {}({}) 요약 생성 완료",
                content.getId().getId(), content.getId().getType());
    }

    /**
     * 리뷰가 5개 이하인 콘텐츠의 요약 제거
     */
    private void removeInsufficientReviewSummaries() {
        log.info("리뷰 부족 콘텐츠 요약 제거 작업 시작");

        List<ReviewSummary> allSummaries = summaryRepository.findAll();

        for (ReviewSummary summary : allSummaries) {
            ContentId contentId = summary.getContent().getId();
            Integer currentReviewCount = reviewRepository.countByContentId(contentId);

            if (currentReviewCount <= 5) {
                summaryRepository.delete(summary);
                log.info("콘텐츠 {}({}) 요약 제거 - 현재 리뷰 {}개",
                        contentId.getId(), contentId.getType(), currentReviewCount);
            }
        }

        log.info("리뷰 부족 콘텐츠 요약 제거 작업 완료");
    }
}
