package com.popcoadmin.event.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class EventQuestionRequestDto {
    private String text;
    private Integer sortOrder;
    private String imgPath;
    private LocalDateTime finishedAt;
    private List<EventOptionRequestDto> options;
}
