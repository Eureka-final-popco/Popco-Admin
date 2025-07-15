package com.popcoadmin.quiz.dto.response;

import com.popcoadmin.quiz.entity.Quiz;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResponseDto {
    private Long quizId;
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String bannerPath;
    private Integer roundCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<QuizQuestionResponseDto> questions;

    public static QuizResponseDto from(Quiz quiz) {
        List<QuizQuestionResponseDto> questionDtos = null;
        if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
            questionDtos = quiz.getQuestions().stream()
                    .map(QuizQuestionResponseDto::from)
                    .collect(Collectors.toList());
        }

        return QuizResponseDto.builder()
                .quizId(quiz.getQuizId())
                .name(quiz.getName())
                .startAt(quiz.getStartAt())
                .endAt(quiz.getEndAt())
                .bannerPath(quiz.getBannerPath())
                .roundCount(quiz.getRoundCount())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .questions(questionDtos)
                .build();
    }
}
