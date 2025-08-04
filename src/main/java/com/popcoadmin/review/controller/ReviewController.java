package com.popcoadmin.review.controller;

import com.popcoadmin.common.response.ApiResponse;
import com.popcoadmin.review.service.ReviewService;
import com.popcoadmin.review.service.ReviewSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
@Tag(name = "Review API", description = "리뷰와 관련된 api 요청이 모두 포함되어 있습니다.")
@RequiredArgsConstructor
public class ReviewController {

    // git 에 추가 됩니다.
    private final ReviewService reviewService;
    private final ReviewSummaryService reviewSummaryService;

    @Operation(summary = "최근 뜨고 있는 리뷰 수동 갱신", description = "관리자가 수동으로 인기 리뷰를 갱신합니다.")
    @PostMapping("/weekly-trend/refresh")
    public ResponseEntity<ApiResponse<Void>> refreshTrendingReviews() {
        reviewService.calculateAndSaveTrendingReviews();
        return ResponseEntity.ok(ApiResponse.success("인기 리뷰가 갱신되었습니다.", null));
    }

    @Operation(summary = "리뷰 요약 수동 갱신", description = "관리자가 수동으로 리뷰 요약을 갱신합니다.")
    @PostMapping("/review-summary")
    public ResponseEntity<ApiResponse<Void>> triggerReviewSummary() {
        reviewSummaryService.processReviewSummaries();
        return ResponseEntity.ok(ApiResponse.success("리뷰 요약 수동 실행 완료", null));
    }

}
