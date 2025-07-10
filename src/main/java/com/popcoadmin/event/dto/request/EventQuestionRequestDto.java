package com.popcoadmin.event.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventQuestionRequestDto {
    private Long eventId;
    private String text;
    private Integer sortOrder;
    private String imgPath;
    private LocalDateTime finishedAt;
}
