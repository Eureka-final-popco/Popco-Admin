package com.popcoadmin.review.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LLMAnalysisResult {
    private String summary;      // 리뷰 요약
    private String evaluation;   // 전체적인 평가 (POSITIVE/NEGATIVE/NEUTRAL)
}
