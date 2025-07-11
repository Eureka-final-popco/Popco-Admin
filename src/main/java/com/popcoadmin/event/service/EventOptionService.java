package com.popcoadmin.event.service;

import com.popcoadmin.event.dto.request.EventOptionRequestDto;
import com.popcoadmin.event.dto.response.EventOptionResponseDto;

import java.util.List;

public interface EventOptionService {
    EventOptionResponseDto createEventOption(Long questionId, EventOptionRequestDto requestDto);
    EventOptionResponseDto getEventOption(Long optionId);
    List<EventOptionResponseDto> getEventOptionsByQuestion(Long questionId);
    EventOptionResponseDto updateEventOption(Long optionId, EventOptionRequestDto requestDto);
    void deleteEventOption(Long optionId);
}
