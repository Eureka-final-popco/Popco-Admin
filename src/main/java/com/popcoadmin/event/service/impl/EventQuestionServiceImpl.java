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
                .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다. ID: " + eventId));

        validateSortOrderUniqueness(eventId, requestDto.getSortOrder(), null);

        EventQuestion question = EventQuestion.builder()
                .text(requestDto.getText())
                .sortOrder(requestDto.getSortOrder())
                .imgPath(requestDto.getImgPath())
                .finishedAt(requestDto.getFinishedAt())
                .event(event)
                .build();

        EventQuestion savedQuestion = eventQuestionRepository.save(question);
        return EventQuestionResponseDto.from(savedQuestion);
    }

    @Override
    @Transactional
    public EventQuestionResponseDto getEventQuestion(Long questionId) {
        EventQuestion question = eventQuestionRepository.findById(questionId)
                .orElseThrow(() -> new EventQuestionNotFoundException("이벤트 질문을 찾을 수 없습니다. ID: " + questionId)); // 직접 한글 메시지 작성 + ID
        return EventQuestionResponseDto.from(question);
    }

    @Override
    @Transactional
    public List<EventQuestionResponseDto> getEventQuestionsByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다. ID: " + eventId)); // 직접 한글 메시지 작성 + ID

        return eventQuestionRepository.findByEvent_EventIdOrderBySortOrderAsc(eventId).stream()
                .map(EventQuestionResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventQuestionResponseDto updateEventQuestion(Long questionId, EventQuestionRequestDto requestDto) {
        EventQuestion question = eventQuestionRepository.findById(questionId)
                .orElseThrow(() -> new EventQuestionNotFoundException("이벤트 질문을 찾을 수 없습니다. ID: " + questionId));

        if (!question.getSortOrder().equals(requestDto.getSortOrder())) {
            validateSortOrderUniqueness(question.getEvent().getEventId(), requestDto.getSortOrder(), questionId);
        }

        question.setText(requestDto.getText());
        question.setSortOrder(requestDto.getSortOrder());
        question.setImgPath(requestDto.getImgPath());
        question.setFinishedAt(requestDto.getFinishedAt());

        return EventQuestionResponseDto.from(question);
    }

    @Override
    @Transactional
    public void deleteEventQuestion(Long questionId) {
        EventQuestion question = eventQuestionRepository.findById(questionId)
                .orElseThrow(() -> new EventQuestionNotFoundException("이벤트 질문을 찾을 수 없습니다. ID: " + questionId));
        eventQuestionRepository.delete(question);
    }

    private void validateSortOrderUniqueness(Long eventId, Integer sortOrder, Long currentQuestionId) {
        Optional<EventQuestion> existingQuestion = eventQuestionRepository.findByEvent_EventIdAndSortOrder(eventId, sortOrder);

        if (existingQuestion.isPresent()) {
            if (currentQuestionId == null || !existingQuestion.get().getQuestionId().equals(currentQuestionId)) {
                throw new DuplicateSortOrderException("해당 이벤트에 이미 동일한 순서(sortOrder)의 질문이 존재합니다.");
            }
        }
    }
}
