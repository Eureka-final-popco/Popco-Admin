package com.popcoadmin.review.gemini;

import com.popcoadmin.content.entity.Content;
import com.popcoadmin.content.entity.ContentGenre;
import com.popcoadmin.review.dto.response.ReviewRatingDistributionDto;
import com.popcoadmin.review.entity.Review;
import com.popcoadmin.review.gemini.dto.*;
import com.popcoadmin.review.gemini.dto.enums.SummaryStrategyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLMAnalysisService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public LLMAnalysisResult analyzeReviews(LLMAnalysisRequest llmRequest) {
        String requestText = null;
        if (llmRequest.getStrategyType() == SummaryStrategyType.INITIAL){
            requestText = createReviewAnalysisPrompt(llmRequest.getReviews(), llmRequest.getContent(), llmRequest.getGenres());
        } else {
            requestText = updateReviewAnalysisPrompt(llmRequest.getReviews(), llmRequest.getContent(), llmRequest.getGenres(), llmRequest.getReviewSummaryDto());
        }

        GeminiRequestDto request = new GeminiRequestDto();
        request.addContent(requestText);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-goog-api-key", geminiApiKey);

        HttpEntity<GeminiRequestDto> entity = new HttpEntity<>(request, headers);

        try {
            GeminiResponseDto response = restTemplate.postForObject(GEMINI_URL, entity, GeminiResponseDto.class);

            if (response == null || response.getCandidates().isEmpty()) {
                log.error("Gemini API 응답이 비어있음");
                throw new RuntimeException();
            }

            String aiResponse = response.getCandidates().get(0).getContent().getParts().get(0).getText();
            return parseAnalysisResult(aiResponse);

        } catch (Exception e) {
            log.error("Failed to call Gemini API", e);
            throw new RuntimeException("Failed to analyze reviews: " + e.getMessage(), e);
        }
    }

    private String createReviewAnalysisPrompt(List<Review> reviews, Content content, List<ContentGenre> genres) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 리뷰들을 분석하여 요약해줘.\n\n");
        prompt.append("리뷰 목록:\n");

        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);
            prompt.append(String.format("%d. [평점: %s점] %s\n",
                    i + 1, review.getScore(), review.getContent()));
        }

        prompt.append("\n콘텐츠 줄거리와 장르는 리뷰 해석의 참고 자료일 뿐, 요약에 포함하지 말고 간단히 참고만 해.\n");
        prompt.append(String.format("제목: %s, 줄거리: %s\n",content.getTitle(), content.getOverview()));
        prompt.append(String.format("장르: ",content.getTitle(), content.getOverview()));
        for (int i = 0; i < genres.size(); i++) {
            prompt.append(String.format("%s, ", genres.get(i).getGenre().getName()));
        }
        prompt.append("\n\n");

        prompt.append("요구사항:\n");
        prompt.append("1. 리뷰 내용을 긍정적, 부정적 의견을 균형 있게 반영하여 200자 이내로 간결하게 요약해줘.\n");
        prompt.append("2. 누구에게 이 작품이 어울리는지 추천 관객층도 포함해줘.\n");
        prompt.append("3. 'SUMMARY' 에는 콘텐츠 줄거리나 장르에 관한 설명을 절대 포함하지 말고, 오직 리뷰에서 나온 긍정적/부정적 의견과 추천 관객층만 작성해.\n");
        prompt.append("4. 문체는 친구에게 추천하듯 편안하게 써줘.\n");
        prompt.append("5. 결과는 다음 형식으로 출력해줘:\n");
        prompt.append("SUMMARY: [200자 이내의 리뷰 요약 및 긍정/부정 요점, 추천 관객층 포함]\n");
        prompt.append("EVALUATION: [긍정/부정/보통 중 하나]\n");

        return prompt.toString();
    }

    private String updateReviewAnalysisPrompt(
            List<Review> newReviews, Content content, List<ContentGenre> genres, ReviewSummaryDto reviewSummaryDto) {
        StringBuilder prompt = new StringBuilder();
        // ✅ 기존 요약 정보 안내
        prompt.append("다음은 이전에 요약된 리뷰 정보야:\n");
        prompt.append(String.format("- 기존 요약: %s\n", reviewSummaryDto.getExistingSummaryText()));
        prompt.append(String.format("- 기존 리뷰 수: %d개, 평균 평점: %.1f점\n",
                reviewSummaryDto.getExistingReviewCount(), reviewSummaryDto.getExistingAvgScore()));

        prompt.append("- 기존 평점 분포:\n");
        reviewSummaryDto.getRatingDistribution().stream()
                .sorted(Comparator.comparing(ReviewRatingDistributionDto::getRating, Comparator.reverseOrder())) // 5점부터 출력
                .forEach(rating -> prompt.append(String.format("  %f점: %d개\n",
                        rating.getRating(), rating.getCount())));

        prompt.append("\n이후, 새롭게 추가된 리뷰는 다음과 같아:\n\n");

        // ✅ 새 리뷰 목록
        for (int i = 0; i < newReviews.size(); i++) {
            Review review = newReviews.get(i);
            prompt.append(String.format("%d. [평점: %s점] %s\n",
                    i + 1, review.getScore(), review.getContent()));
        }

        // ✅ 콘텐츠 정보
        prompt.append("\n콘텐츠 줄거리와 장르는 리뷰 해석의 참고 자료일 뿐, 요약에 포함하지 말고 참고만 해.\n");
        prompt.append(String.format("제목: %s, 줄거리: %s\n", content.getTitle(), content.getOverview()));
        prompt.append("장르: ");
        for (int i = 0; i < genres.size(); i++) {
            prompt.append(String.format("%s", genres.get(i).getGenre().getName()));
            if (i < genres.size() - 1) prompt.append(", ");
        }
        prompt.append("\n\n");

        // ✅ 요약 지시사항
        prompt.append("요구사항:\n");
        prompt.append("1. 이전 요약 내용을 참고하여 전체 리뷰의 핵심 의견과 새 리뷰 내용을 통합적으로 반영해.\n");
        prompt.append("2. 리뷰 내용을 긍정적, 부정적 의견을 균형 있게 반영하여 200자 이내로 간결하게 요약해줘.\n");
        prompt.append("3. 누구에게 이 작품이 어울리는지 추천 관객층도 포함해줘.\n");
        prompt.append("4. 'SUMMARY' 에는 콘텐츠 줄거리나 장르에 관한 설명을 절대 포함하지 말고, 오직 리뷰에서 나온 긍정적/부정적 의견과 추천 관객층만 작성해.\n");
        prompt.append("5. 'EVALUATION' 무조건 긍정/부정/보통 중 하나만 선택해. 다른 절대 평가를 포함하지마\n");
        prompt.append("6. 문체는 친구에게 추천하듯 편안하게 써줘.\n");
        prompt.append("7. 결과는 다음 형식으로 출력해줘:\n");
        prompt.append("SUMMARY: [200자 이내의 리뷰 요약 및 긍정/부정 요점, 추천 관객층 포함]\n");
        prompt.append("EVALUATION: [긍정/부정/보통 중 하나]\n");

        return prompt.toString();
    }

    private LLMAnalysisResult parseAnalysisResult(String aiResponse) {
        try {
            String summary = "";
            String evaluation = "보통";

            String[] lines = aiResponse.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("SUMMARY:")) {
                    summary = line.substring("SUMMARY:".length()).trim();
                } else if (line.startsWith("EVALUATION:")) {
                    evaluation = line.substring("EVALUATION:".length()).trim();
                }
            }

            // 파싱 실패 시 기본값 설정
            if (summary.isEmpty()) {
                summary = aiResponse.length() > 200 ?
                        aiResponse.substring(0, 200) + "..." : aiResponse;
            }

            return LLMAnalysisResult.builder()
                    .summary(summary)
                    .evaluation(evaluation)
                    .build();

        } catch (Exception e) {
            log.error("AI 응답 파싱 중 오류 발생: {}", aiResponse, e);

            // 파싱 실패 시 전체 응답을 요약으로 사용
            String fallbackSummary = aiResponse.length() > 200 ?
                    aiResponse.substring(0, 200) + "..." : aiResponse;

            return LLMAnalysisResult.builder()
                    .summary(fallbackSummary)
                    .evaluation("보통")
                    .build();
        }
    }
}
