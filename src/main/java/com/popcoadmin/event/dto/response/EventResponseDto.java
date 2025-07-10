package com.popcoadmin.event.dto.response;

import com.popcoadmin.event.entity.Event;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseDto {
    private Long eventId;
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String bannerPath;
    private List<EventQuestionResponseDto> questions;

    public static EventResponseDto from(Event event) {
        return EventResponseDto.builder()
                .eventId(event.getEventId())
                .name(event.getName())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .bannerPath(event.getBannerPath())
                .questions(event.getQuestions() != null ?
                        event.getQuestions().stream()
                                .map(EventQuestionResponseDto::from)
                                .collect(Collectors.toList()) : null)
                .build();
    }

    public static EventResponseDto fromWithoutQuestions(Event event) {
        return EventResponseDto.builder()
                .eventId(event.getEventId())
                .name(event.getName())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .bannerPath(event.getBannerPath())
                .build();
    }
}
