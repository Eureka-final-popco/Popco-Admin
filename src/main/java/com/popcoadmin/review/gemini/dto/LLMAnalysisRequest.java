package com.popcoadmin.review.gemini.dto;

import com.popcoadmin.content.entity.Content;
import com.popcoadmin.content.entity.ContentGenre;
import com.popcoadmin.review.entity.Review;
import com.popcoadmin.review.gemini.dto.enums.SummaryStrategyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LLMAnalysisRequest {
    private List<Review> reviews; // 새로운 리뷰 or 전체 리뷰
    private Content content;
    private List<ContentGenre> genres;
    private ReviewSummaryDto reviewSummaryDto;
    private SummaryStrategyType strategyType;

    public static LLMAnalysisRequest ofInitial(Content content, List<ContentGenre> contentGenre, List<Review> reviews, SummaryStrategyType strategyType) {
        return LLMAnalysisRequest.builder()
                .reviews(reviews)
                .content(content)
                .genres(contentGenre)
                .strategyType(strategyType)
                .build();
    }

    public static LLMAnalysisRequest ofUpdate(List<Review> recentReviews, Content content, List<ContentGenre> contentGenre, ReviewSummaryDto reviewSummaryDto, SummaryStrategyType strategyType) {
        return LLMAnalysisRequest.builder()
                .reviews(recentReviews)
                .content(content)
                .genres(contentGenre)
                .strategyType(strategyType)
                .reviewSummaryDto(reviewSummaryDto)
                .build();
    }
}

