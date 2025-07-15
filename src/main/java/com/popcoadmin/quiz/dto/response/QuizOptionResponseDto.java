package com.popcoadmin.quiz.dto.response;

import com.popcoadmin.quiz.entity.QuizOption;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizOptionResponseDto {
    private Long optionId;
    private Long questionId;
    private Long quizId;
    private String content;
    private Boolean isCorrect;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static QuizOptionResponseDto from(QuizOption option) {
        return QuizOptionResponseDto.builder()
                .optionId(option.getOptionId())
                .questionId(option.getQuizQuestion() != null ? option.getQuizQuestion().getQuestionId() : null)
                .quizId(option.getQuizQuestion() != null && option.getQuizQuestion().getQuiz() != null ?
                        option.getQuizQuestion().getQuiz().getQuizId() : null)
                .content(option.getContent())
                .isCorrect(option.getIsCorrect())
                .createdAt(option.getCreatedAt())
                .updatedAt(option.getUpdatedAt())
                .build();
    }
}
