package com.popcoadmin.event.service.impl;

import com.popcoadmin.event.dto.request.EventQuestionRequestDto;
import com.popcoadmin.event.dto.response.EventQuestionResponseDto;
import com.popcoadmin.event.entity.Event;
import com.popcoadmin.event.entity.EventQuestion;
import com.popcoadmin.event.repository.EventQuestionRepository;
import com.popcoadmin.event.repository.EventRepository;
import com.popcoadmin.event.service.EventQuestionService;
import com.popcoadmin.exception.business.EventNotFoundException;
import com.popcoadmin.exception.business.EventQuestionNotFoundException;
import com.popcoadmin.exception.business.DuplicateSortOrderException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventQuestionServiceImpl implements EventQuestionService {

    private final EventQuestionRepository eventQuestionRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventQuestionResponseDto createEventQuestion(Long eventId, EventQuestionRequestDto requestDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        validateSortOrderUniqueness(eventId, requestDto.getSortOrder(), null);

        EventQuestion question = EventQuestion.builder()
                .text(requestDto.getText())
                .sortOrder(requestDto.getSortOrder())
                .imgPath(requestDto.getImgPath())
                .finishedAt(requestDto.getFinishedAt())
                .event(event)
                .build();

        EventQuestion savedQuestion = eventQuestionRepository.save(question);
        return EventQuestionResponseDto.of(savedQuestion);
    }

    @Override
    @Transactional
    public EventQuestionResponseDto getEventQuestion(Long questionId) {
        EventQuestion question = eventQuestionRepository.findById(questionId)
                .orElseThrow(EventQuestionNotFoundException::new);
        return EventQuestionResponseDto.of(question);
    }

    @Override
    @Transactional
    public List<EventQuestionResponseDto> getEventQuestionsByEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event with ID " + eventId + " not found.");
        }
        return eventQuestionRepository.findByEvent_EventIdOrderBySortOrderAsc(eventId).stream()
                .map(EventQuestionResponseDto::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventQuestionResponseDto updateEventQuestion(Long questionId, EventQuestionRequestDto requestDto) {
        EventQuestion question = eventQuestionRepository.findById(questionId)
                .orElseThrow(EventQuestionNotFoundException::new);

        if (!question.getSortOrder().equals(requestDto.getSortOrder())) {
            validateSortOrderUniqueness(question.getEvent().getEventId(), requestDto.getSortOrder(), questionId);
        }

        question.setText(requestDto.getText());
        question.setSortOrder(requestDto.getSortOrder());
        question.setImgPath(requestDto.getImgPath());
        question.setFinishedAt(requestDto.getFinishedAt());

        return EventQuestionResponseDto.of(question);
    }

    @Override
    @Transactional
    public void deleteEventQuestion(Long questionId) {
        EventQuestion question = eventQuestionRepository.findById(questionId)
                .orElseThrow(EventQuestionNotFoundException::new);
        eventQuestionRepository.delete(question);
    }

    private void validateSortOrderUniqueness(Long eventId, Integer sortOrder, Long currentQuestionId) {
        Optional<EventQuestion> existingQuestion = eventQuestionRepository.findByEvent_EventIdAndSortOrder(eventId, sortOrder);

        if (existingQuestion.isPresent()) {
            if (currentQuestionId == null || !existingQuestion.get().getQuestionId().equals(currentQuestionId)) {
                throw new DuplicateSortOrderException("EventId: " + eventId + ", SortOrder: " + sortOrder);
            }
        }
    }
}
