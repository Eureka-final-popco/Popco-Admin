package com.popcoadmin.event.controller;

import com.popcoadmin.common.response.ApiResponse;
import com.popcoadmin.event.dto.request.EventQuestionRequestDto;
import com.popcoadmin.event.dto.response.EventQuestionResponseDto;
import com.popcoadmin.event.service.EventQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Event Question", description = "이벤트 질문 관련 API")
@RestController
@RequestMapping("/event/{eventId}/question")
@RequiredArgsConstructor
public class EventQuestionController {

    private final EventQuestionService eventQuestionService;

    @Operation(summary = "특정 이벤트에 질문 생성", description = "지정된 이벤트에 새로운 질문을 추가합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<EventQuestionResponseDto>> createEventQuestion(
            @PathVariable Long eventId,
            @RequestBody EventQuestionRequestDto requestDto) {
        EventQuestionResponseDto createdQuestion = eventQuestionService.createEventQuestion(eventId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("질문이 성공적으로 생성되었습니다.", createdQuestion));
    }

    @Operation(summary = "특정 이벤트의 특정 질문 조회", description = "지정된 이벤트 내에서 특정 질문 ID의 정보를 조회합니다.")
    @GetMapping("/{questionId}")
    public ResponseEntity<ApiResponse<EventQuestionResponseDto>> getEventQuestion(
            @PathVariable Long eventId,
            @PathVariable Long questionId) {
        EventQuestionResponseDto question = eventQuestionService.getEventQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.success("질문 조회 성공", question));
    }

    @Operation(summary = "특정 이벤트의 모든 질문 조회", description = "지정된 이벤트에 속한 모든 질문 목록을 정렬 순서에 따라 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventQuestionResponseDto>>> getEventQuestionsByEvent(@PathVariable Long eventId) {
        List<EventQuestionResponseDto> questions = eventQuestionService.getEventQuestionsByEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("모든 질문 조회 성공", questions));
    }

    @Operation(summary = "질문 정보 수정", description = "지정된 이벤트 내에서 특정 질문 ID의 정보를 수정합니다.")
    @PutMapping("/{questionId}")
    public ResponseEntity<ApiResponse<EventQuestionResponseDto>> updateEventQuestion(
            @PathVariable Long eventId,
            @PathVariable Long questionId,
            @RequestBody EventQuestionRequestDto requestDto) {
        EventQuestionResponseDto updatedQuestion = eventQuestionService.updateEventQuestion(questionId, requestDto);
        return ResponseEntity.ok(ApiResponse.success("질문 수정 성공", updatedQuestion));
    }

    @Operation(summary = "질문 삭제", description = "지정된 이벤트 내에서 특정 질문 ID의 정보를 삭제합니다.")
    @DeleteMapping("/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteEventQuestion(
            @PathVariable Long eventId,
            @PathVariable Long questionId) {
        eventQuestionService.deleteEventQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
