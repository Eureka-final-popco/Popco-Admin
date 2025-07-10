package com.popcoadmin.event.service.impl;

import com.popcoadmin.event.dto.request.EventRequestDto;
import com.popcoadmin.event.dto.response.EventResponseDto;
import com.popcoadmin.event.entity.Event;
import com.popcoadmin.event.repository.EventRepository;
import com.popcoadmin.event.service.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.popcoadmin.exception.business.EventNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventResponseDto createEvent(EventRequestDto requestDto) {
        Event event = Event.builder()
                .name(requestDto.getName())
                .startAt(requestDto.getStartAt())
                .endAt(requestDto.getEndAt())
                .bannerPath(requestDto.getBannerPath())
                .build();
        Event savedEvent = eventRepository.save(event);
        return EventResponseDto.from(savedEvent);
    }

    @Override
    @Transactional
    public EventResponseDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        return EventResponseDto.from(event);
    }

    @Override
    @Transactional
    public List<EventResponseDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(EventResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public EventResponseDto updateEvent(Long eventId, EventRequestDto requestDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        event.setName(requestDto.getName());
        event.setStartAt(requestDto.getStartAt());
        event.setEndAt(requestDto.getEndAt());
        event.setBannerPath(requestDto.getBannerPath());
        return EventResponseDto.from(event);
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        eventRepository.delete(event);
    }
}
