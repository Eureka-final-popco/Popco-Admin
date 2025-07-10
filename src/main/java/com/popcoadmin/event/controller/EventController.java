package com.popcoadmin.event.controller;

import com.popcoadmin.event.dto.request.EventOptionRequestDto;
import com.popcoadmin.event.dto.request.EventQuestionRequestDto;
import com.popcoadmin.event.dto.request.EventRequestDto;
import com.popcoadmin.event.dto.response.EventOptionResponseDto;
import com.popcoadmin.event.dto.response.EventQuestionResponseDto;
import com.popcoadmin.event.dto.response.EventResponseDto;
import com.popcoadmin.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
@Tag(name = "관리자용 이벤트 API", description = "이벤트, 질문, 선택지 관련 CRUD API")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "새 이벤트 생성", description = "새로운 이벤트를 생성합니다.")
    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventRequestDto request) {
        EventResponseDto response = eventService.createEvent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "모든 이벤트 조회", description = "모든 이벤트의 요약 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        List<EventResponseDto> events = eventService.getAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @Operation(summary = "특정 이벤트 조회", description = "ID를 통해 특정 이벤트의 정보를 조회합니다.")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long eventId) {
        EventResponseDto event = eventService.getEventById(eventId);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @Operation(summary = "특정 이벤트 상세 조회", description = "ID를 통해 특정 이벤트의 상세 정보(질문, 선택지 포함)를 조회합니다.")
    @GetMapping("/{eventId}/detail")
    public ResponseEntity<EventResponseDto> getEventDetailById(@PathVariable Long eventId) {
        EventResponseDto eventDetail = eventService.getEventDetailById(eventId);
        return new ResponseEntity<>(eventDetail, HttpStatus.OK);
    }

    @Operation(summary = "이벤트 정보 수정", description = "ID를 통해 특정 이벤트의 정보를 수정합니다.")
    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long eventId, @RequestBody EventRequestDto request) {
        EventResponseDto updatedEvent = eventService.updateEvent(eventId, request);
        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
    }

    @Operation(summary = "이벤트 삭제", description = "ID를 통해 특정 이벤트를 삭제합니다. 연관된 질문과 선택지도 함께 삭제됩니다.")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "이벤트 질문 생성", description = "특정 이벤트에 새로운 질문을 생성합니다.")
    @PostMapping("/{eventId}/questions")
    public ResponseEntity<EventQuestionResponseDto> createQuestion(@PathVariable Long eventId, @RequestBody EventQuestionRequestDto request) {
        EventQuestionResponseDto response = eventService.createQuestion(eventId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "특정 질문 조회", description = "ID를 통해 특정 질문의 요약 정보를 조회합니다.")
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<EventQuestionResponseDto> getQuestionById(@PathVariable Long questionId) {
        EventQuestionResponseDto question = eventService.getQuestionById(questionId);
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    @Operation(summary = "특정 이벤트의 모든 질문 조회", description = "특정 이벤트 ID에 속하는 모든 질문의 정보를 조회합니다.")
    @GetMapping("/{eventId}/questions")
    public ResponseEntity<List<EventQuestionResponseDto>> getQuestionsByEventId(@PathVariable Long eventId) {
        List<EventQuestionResponseDto> questions = eventService.getQuestionsByEventId(eventId);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @Operation(summary = "질문 정보 수정", description = "ID를 통해 특정 질문의 정보를 수정합니다.")
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<EventQuestionResponseDto> updateQuestion(@PathVariable Long questionId, @RequestBody EventQuestionRequestDto request) {
        EventQuestionResponseDto updatedQuestion = eventService.updateQuestion(questionId, request);
        return new ResponseEntity<>(updatedQuestion, HttpStatus.OK);
    }

    @Operation(summary = "질문 삭제", description = "ID를 통해 특정 질문을 삭제합니다. 연관된 선택지도 함께 삭제됩니다.")
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        eventService.deleteQuestion(questionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "질문 선택지 생성", description = "특정 질문에 새로운 선택지를 생성합니다.")
    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<EventOptionResponseDto> createOption(@PathVariable Long questionId, @RequestBody EventOptionRequestDto request) {
        EventOptionResponseDto response = eventService.createOption(questionId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "특정 선택지 조회", description = "ID를 통해 특정 선택지를 조회합니다.")
    @GetMapping("/options/{optionId}")
    public ResponseEntity<EventOptionResponseDto> getOptionById(@PathVariable Long optionId) {
        EventOptionResponseDto option = eventService.getOptionById(optionId);
        return new ResponseEntity<>(option, HttpStatus.OK);
    }

    @Operation(summary = "특정 질문의 모든 선택지 조회", description = "특정 질문 ID에 속하는 모든 선택지를 조회합니다.")
    @GetMapping("/questions/{questionId}/options")
    public ResponseEntity<List<EventOptionResponseDto>> getOptionsByQuestionId(@PathVariable Long questionId) {
        List<EventOptionResponseDto> options = eventService.getOptionsByQuestionId(questionId);
        return new ResponseEntity<>(options, HttpStatus.OK);
    }

    @Operation(summary = "선택지 정보 수정", description = "ID를 통해 특정 선택지의 정보를 수정합니다.")
    @PutMapping("/options/{optionId}")
    public ResponseEntity<EventOptionResponseDto> updateOption(@PathVariable Long optionId, @RequestBody EventOptionRequestDto request) {
        EventOptionResponseDto updatedOption = eventService.updateOption(optionId, request);
        return new ResponseEntity<>(updatedOption, HttpStatus.OK);
    }

    @Operation(summary = "선택지 삭제", description = "ID를 통해 특정 선택지를 삭제합니다.")
    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long optionId) {
        eventService.deleteOption(optionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
