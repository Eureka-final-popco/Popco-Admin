package com.popcoadmin.event.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EventOptionRequestDto {
    private String text;
    private Boolean isCorrect;
}
