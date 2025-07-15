package com.popcoadmin.quiz.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizOptionRequestDto {
    private String content;
    private Boolean isCorrect;
}
