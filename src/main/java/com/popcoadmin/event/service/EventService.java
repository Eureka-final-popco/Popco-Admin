package com.popcoadmin.event.service;

import com.popcoadmin.event.dto.request.EventOptionRequestDto;
import com.popcoadmin.event.dto.request.EventQuestionRequestDto;
import com.popcoadmin.event.dto.request.EventRequestDto;
import com.popcoadmin.event.dto.response.EventOptionResponseDto;
import com.popcoadmin.event.dto.response.EventQuestionResponseDto;
import com.popcoadmin.event.dto.response.EventResponseDto;
import com.sun.jdi.request.EventRequest;

import java.util.List;

public interface EventService {
    EventResponseDto createEvent(EventRequestDto request);
    EventResponseDto getEventById(Long eventId);
    EventResponseDto getEventDetailById(Long eventId);
    List<EventResponseDto> getAllEvents();
    EventResponseDto updateEvent(Long eventId, EventRequestDto request);
    void deleteEvent(Long eventId);

    EventQuestionResponseDto createQuestion(Long eventId, EventQuestionRequestDto request);
    EventQuestionResponseDto getQuestionById(Long questionId);
    List<EventQuestionResponseDto> getQuestionsByEventId(Long eventId);
    EventQuestionResponseDto updateQuestion(Long questionId, EventQuestionRequestDto request);
    void deleteQuestion(Long questionId);

    EventOptionResponseDto createOption(Long questionId, EventOptionRequestDto request);
    EventOptionResponseDto getOptionById(Long optionId);
    List<EventOptionResponseDto> getOptionsByQuestionId(Long questionId);
    EventOptionResponseDto updateOption(Long optionId, EventOptionRequestDto request);
    void deleteOption(Long optionId);
}
