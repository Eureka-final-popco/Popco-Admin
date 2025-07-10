package com.popcoadmin.event.controller;

import com.popcoadmin.event.dto.request.EventRequestDto;
import com.popcoadmin.event.dto.response.EventResponseDto;
import com.popcoadmin.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Event", description = "이벤트 관련 API")
@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "새 이벤트 생성", description = "새로운 이벤트를 생성합니다.")
    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventRequestDto requestDto) {
        EventResponseDto createdEvent = eventService.createEvent(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @Operation(summary = "특정 이벤트 조회", description = "ID를 통해 특정 이벤트 정보를 조회합니다.")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEvent(@PathVariable Long eventId) {
        EventResponseDto event = eventService.getEvent(eventId);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "모든 이벤트 조회", description = "등록된 모든 이벤트 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        List<EventResponseDto> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "이벤트 정보 수정", description = "ID를 통해 특정 이벤트 정보를 수정합니다.")
    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long eventId, @RequestBody EventRequestDto requestDto) {
        EventResponseDto updatedEvent = eventService.updateEvent(eventId, requestDto);
        return ResponseEntity.ok(updatedEvent);
    }

    @Operation(summary = "이벤트 삭제", description = "ID를 통해 특정 이벤트를 삭제합니다.")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
