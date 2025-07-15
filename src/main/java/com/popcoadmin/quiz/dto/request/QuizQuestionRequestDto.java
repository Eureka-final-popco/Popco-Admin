package com.popcoadmin.quiz.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionRequestDto {
    private Integer questionOrder;
    private String imgPath;
    private LocalDateTime finishedAt;
    private Integer firstCapacity;
    private String content;

    private List<QuizOptionRequestDto> options;
}
