package com.popcoadmin.event.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventOptionRequestDto {
    private Long questionId;
    private String text;
    private Boolean isCorrect;
}
