package com.popcoadmin.quiz.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequestDto {
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String bannerPath;
    private Integer roundCount;

    private List<QuizQuestionRequestDto> questions;
}
