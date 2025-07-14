package com.popcoadmin.quiz.dto.response;

import com.popcoadmin.quiz.entity.QuizQuestion;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionResponseDto {
    private Long questionId;
    private Long quizId;
    private String content;
    private Integer questionOrder;
    private String imgPath;
    private LocalDateTime finishedAt;
    private Integer firstCapacity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<QuizOptionResponseDto> options;

    public static QuizQuestionResponseDto from(QuizQuestion question) {
        List<QuizOptionResponseDto> optionDtos = null;
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            optionDtos = question.getOptions().stream()
                    .map(QuizOptionResponseDto::from)
                    .collect(Collectors.toList());
        }

        return QuizQuestionResponseDto.builder()
                .questionId(question.getQuestionId())
                .quizId(question.getQuiz() != null ? question.getQuiz().getQuizId() : null)
                .content(question.getContent())
                .questionOrder(question.getQuestionOrder())
                .imgPath(question.getImgPath())
                .finishedAt(question.getFinishedAt())
                .firstCapacity(question.getFirstCapacity())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .options(optionDtos)
                .build();
    }
}
