package com.popcoadmin.event.dto.response;

import com.popcoadmin.event.entity.EventOption;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventOptionResponseDto {
    private Long optionId;
    private Long questionId;
    private String text;
    private Boolean isCorrect;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EventOptionResponseDto of(EventOption option) {
        return EventOptionResponseDto.builder()
                .optionId(option.getOptionId())
                .questionId(option.getQuestion().getQuestionId())
                .text(option.getText())
                .isCorrect(option.getIsCorrect())
                .createdAt(option.getCreatedAt())
                .updatedAt(option.getUpdatedAt())
                .build();
    }
}
