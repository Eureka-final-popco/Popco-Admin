package com.popcoadmin.event.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class EventRequestDto {
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String bannerPath;
    private List<EventQuestionRequestDto> questions;
}
