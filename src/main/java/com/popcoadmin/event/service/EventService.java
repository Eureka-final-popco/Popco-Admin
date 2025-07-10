package com.popcoadmin.event.service;

import com.popcoadmin.event.dto.request.EventRequestDto;
import com.popcoadmin.event.dto.response.EventResponseDto;

import java.util.List;

public interface EventService {
    EventResponseDto createEvent(EventRequestDto requestDto);
    EventResponseDto getEvent(Long eventId);
    List<EventResponseDto> getAllEvents();
    EventResponseDto updateEvent(Long eventId, EventRequestDto requestDto);
    void deleteEvent(Long eventId);
}
