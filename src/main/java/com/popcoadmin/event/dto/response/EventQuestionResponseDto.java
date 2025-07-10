package com.popcoadmin.event.dto.response;

import com.popcoadmin.event.entity.EventQuestion;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class EventQuestionResponseDto {
    private Long questionId;
    private Long eventId;
    private String text;
    private Integer sortOrder;
    private String imgPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime finishedAt;
    private List<EventOptionResponseDto> options;

    public static EventQuestionResponseDto of(EventQuestion question) {
        List<EventOptionResponseDto> optionDtos = null;
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            optionDtos = question.getOptions().stream()
                    .map(EventOptionResponseDto::of)
                    .collect(Collectors.toList());
        }

        return EventQuestionResponseDto.builder()
                .questionId(question.getQuestionId())
                .eventId(question.getEvent().getEventId())
                .text(question.getText())
                .sortOrder(question.getSortOrder())
                .imgPath(question.getImgPath())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .finishedAt(question.getFinishedAt())
                .options(optionDtos)
                .build();
    }
}
