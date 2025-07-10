package com.popcoadmin.event.dto.response;

import com.popcoadmin.event.entity.EventQuestion;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    public static EventQuestionResponseDto from(EventQuestion question) {
        return EventQuestionResponseDto.builder()
                .questionId(question.getQuestionId())
                .eventId(question.getEvent().getEventId())
                .text(question.getText())
                .sortOrder(question.getSortOrder())
                .imgPath(question.getImgPath())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .finishedAt(question.getFinishedAt())
                .options(question.getOptions() != null ?
                        question.getOptions().stream()
                                .map(EventOptionResponseDto::from)
                                .collect(Collectors.toList()) : null)
                .build();
    }

    public static EventQuestionResponseDto fromWithoutOptions(EventQuestion question) {
        return EventQuestionResponseDto.builder()
                .questionId(question.getQuestionId())
                .eventId(question.getEvent().getEventId())
                .text(question.getText())
                .sortOrder(question.getSortOrder())
                .imgPath(question.getImgPath())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .finishedAt(question.getFinishedAt())
                .build();
    }
}
