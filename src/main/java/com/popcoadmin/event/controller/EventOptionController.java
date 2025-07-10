package com.popcoadmin.event.controller;

import com.popcoadmin.event.dto.request.EventOptionRequestDto;
import com.popcoadmin.event.dto.response.EventOptionResponseDto;
import com.popcoadmin.event.service.EventOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Event Option", description = "이벤트 질문 옵션 관련 API")
@RestController
@RequestMapping("/event/{eventId}/question/{questionId}/option")
@RequiredArgsConstructor
public class EventOptionController {

    private final EventOptionService eventOptionService;

    @Operation(summary = "특정 질문에 옵션 생성", description = "지정된 질문에 새로운 옵션을 추가합니다.")
    @PostMapping
    public ResponseEntity<EventOptionResponseDto> createEventOption(@PathVariable Long questionId, @RequestBody EventOptionRequestDto requestDto) {
        EventOptionResponseDto createdOption = eventOptionService.createEventOption(questionId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOption);
    }

    @Operation(summary = "특정 질문의 특정 옵션 조회", description = "지정된 질문 내에서 특정 옵션 ID의 정보를 조회합니다.")
    @GetMapping("/{optionId}")
    public ResponseEntity<EventOptionResponseDto> getEventOption(@PathVariable Long optionId) {
        EventOptionResponseDto option = eventOptionService.getEventOption(optionId);
        return ResponseEntity.ok(option);
    }

    @Operation(summary = "특정 질문의 모든 옵션 조회", description = "지정된 질문에 속한 모든 옵션 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<EventOptionResponseDto>> getEventOptionsByQuestion(@PathVariable Long questionId) {
        List<EventOptionResponseDto> options = eventOptionService.getEventOptionsByQuestion(questionId);
        return ResponseEntity.ok(options);
    }

    @Operation(summary = "옵션 정보 수정", description = "지정된 질문 내에서 특정 옵션 ID의 정보를 수정합니다.")
    @PutMapping("/{optionId}")
    public ResponseEntity<EventOptionResponseDto> updateEventOption(@PathVariable Long optionId, @RequestBody EventOptionRequestDto requestDto) {
        EventOptionResponseDto updatedOption = eventOptionService.updateEventOption(optionId, requestDto);
        return ResponseEntity.ok(updatedOption);
    }

    @Operation(summary = "옵션 삭제", description = "지정된 질문 내에서 특정 옵션 ID의 정보를 삭제합니다.")
    @DeleteMapping("/{optionId}")
    public ResponseEntity<Void> deleteEventOption(@PathVariable Long optionId) {
        eventOptionService.deleteEventOption(optionId);
        return ResponseEntity.noContent().build();
    }
}
