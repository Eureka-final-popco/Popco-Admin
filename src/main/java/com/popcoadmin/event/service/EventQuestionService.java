package com.popcoadmin.event.service;

import com.popcoadmin.event.dto.request.EventQuestionRequestDto;
import com.popcoadmin.event.dto.response.EventQuestionResponseDto;

import java.util.List;

public interface EventQuestionService {
    EventQuestionResponseDto createEventQuestion(Long eventId, EventQuestionRequestDto requestDto);
    EventQuestionResponseDto getEventQuestion(Long questionId);
    List<EventQuestionResponseDto> getEventQuestionsByEvent(Long eventId);
    EventQuestionResponseDto updateEventQuestion(Long questionId, EventQuestionRequestDto requestDto);
    void deleteEventQuestion(Long questionId);
}
