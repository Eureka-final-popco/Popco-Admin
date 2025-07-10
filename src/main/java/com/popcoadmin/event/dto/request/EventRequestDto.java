package com.popcoadmin.event.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestDto {
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String bannerPath;
}
