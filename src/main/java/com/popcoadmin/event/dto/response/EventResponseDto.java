package com.popcoadmin.event.dto.response;

import com.popcoadmin.event.entity.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class EventResponseDto {
    private Long eventId;
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String bannerPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<EventQuestionResponseDto> questions;

    public static EventResponseDto of(Event event) {
        List<EventQuestionResponseDto> questionDtos = null;
        if (event.getQuestions() != null && !event.getQuestions().isEmpty()) {
            questionDtos = event.getQuestions().stream()
                    .map(EventQuestionResponseDto::of)
                    .collect(Collectors.toList());
        }

        return EventResponseDto.builder()
                .eventId(event.getEventId())
                .name(event.getName())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .bannerPath(event.getBannerPath())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .questions(questionDtos)
                .build();
    }
}
