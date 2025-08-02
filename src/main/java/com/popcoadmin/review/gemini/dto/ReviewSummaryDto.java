package com.popcoadmin.review.gemini.dto;

import com.popcoadmin.review.dto.response.ReviewRatingDistributionDto;
import com.popcoadmin.review.entity.Review;
import com.popcoadmin.review.entity.ReviewSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSummaryDto {
    private String existingSummaryText;
    private BigDecimal existingAvgScore;
    private Long existingReviewCount;
    private List<ReviewRatingDistributionDto> ratingDistribution;

    public static ReviewSummaryDto of(ReviewSummary reviewSummary, List<ReviewRatingDistributionDto> ratingDistribution) {
        return ReviewSummaryDto.builder()
                .existingSummaryText(reviewSummary.getSummaryText())
                .existingAvgScore(reviewSummary.getReviewAvg())
                .existingReviewCount(reviewSummary.getReviewCount())
                .ratingDistribution(ratingDistribution)
                .build();
    }
}
