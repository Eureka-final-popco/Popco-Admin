package com.popcoadmin.event.service.impl;

import com.popcoadmin.event.dto.request.EventOptionRequestDto;
import com.popcoadmin.event.dto.request.EventQuestionRequestDto;
import com.popcoadmin.event.dto.request.EventRequestDto;
import com.popcoadmin.event.dto.response.EventOptionResponseDto;
import com.popcoadmin.event.dto.response.EventQuestionResponseDto;
import com.popcoadmin.event.dto.response.EventResponseDto;
import com.popcoadmin.event.entity.Event;
import com.popcoadmin.event.entity.EventOption;
import com.popcoadmin.event.entity.EventQuestion;
import com.popcoadmin.event.repository.EventOptionRepository;
import com.popcoadmin.event.repository.EventQuestionRepository;
import com.popcoadmin.event.repository.EventRepository;
import com.popcoadmin.event.service.EventService;
import com.popcoadmin.exception.business.EventNotFoundException;
import com.popcoadmin.exception.business.EventQuestionNotFoundException;
import com.popcoadmin.exception.business.EventOptionNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventQuestionRepository eventQuestionRepository;
    private final EventOptionRepository eventOptionRepository;

    @Override
    @Transactional
    public EventResponseDto createEvent(EventRequestDto request) {
        Event event = Event.builder()
                .name(request.getName())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .bannerPath(request.getBannerPath())
                .build();
        return EventResponseDto.from(eventRepository.save(event));
    }

    @Override
    public EventResponseDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        return EventResponseDto.fromWithoutQuestions(event);
    }

    @Override
    public EventResponseDto getEventDetailById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        return EventResponseDto.from(event);
    }

    @Override
    public List<EventResponseDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(EventResponseDto::fromWithoutQuestions)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventResponseDto updateEvent(Long eventId, EventRequestDto request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        event.setName(request.getName());
        event.setStartAt(request.getStartAt());
        event.setEndAt(request.getEndAt());
        event.setBannerPath(request.getBannerPath());

        return EventResponseDto.from(eventRepository.save(event));
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        eventRepository.delete(event);
    }

    @Override
    @Transactional
    public EventQuestionResponseDto createQuestion(Long eventId, EventQuestionRequestDto request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        EventQuestion question = EventQuestion.builder()
                .event(event)
                .text(request.getText())
                .sortOrder(request.getSortOrder())
                .imgPath(request.getImgPath())
                .finishedAt(request.getFinishedAt())
                .build();
        return EventQuestionResponseDto.from(eventQuestionRepository.save(question));
    }

    @Override
    public EventQuestionResponseDto getQuestionById(Long questionId) {
        EventQuestion question = eventQuestionRepository.findById(questionId)
                .orElseThrow(EventQuestionNotFoundException::new);
        return EventQuestionResponseDto.fromWithoutOptions(question);
    }

    @Override
    public List<EventQuestionResponseDto> getQuestionsByEventId(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        return event.getQuestions().stream()
                .map(EventQuestionResponseDto::fromWithoutOptions)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventQuestionResponseDto updateQuestion(Long questionId, EventQuestionRequestDto request) {
        EventQuestion question = eventQuestionRepository.findById(questionId)
                .orElseThrow(EventQuestionNotFoundException::new);

        question.setText(request.getText());
        question.setSortOrder(request.getSortOrder());
        question.setImgPath(request.getImgPath());
        question.setFinishedAt(request.getFinishedAt());

        return EventQuestionResponseDto.from(eventQuestionRepository.save(question));
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        EventQuestion question = eventQuestionRepository.findById(questionId)
                .orElseThrow(EventQuestionNotFoundException::new);
        eventQuestionRepository.delete(question);
    }

    @Override
    @Transactional
    public EventOptionResponseDto createOption(Long questionId, EventOptionRequestDto request) {
        EventQuestion question = eventQuestionRepository.findById(questionId)
                .orElseThrow(EventQuestionNotFoundException::new);

        EventOption option = EventOption.builder()
                .question(question) // 연관 관계 설정
                .text(request.getText())
                .isCorrect(request.getIsCorrect())
                .build();
        return EventOptionResponseDto.from(eventOptionRepository.save(option));
    }

    @Override
    public EventOptionResponseDto getOptionById(Long optionId) {
        EventOption option = eventOptionRepository.findById(optionId)
                .orElseThrow(EventOptionNotFoundException::new);
        return EventOptionResponseDto.from(option);
    }

    @Override
    public List<EventOptionResponseDto> getOptionsByQuestionId(Long questionId) {
        EventQuestion question = eventQuestionRepository.findById(questionId)
                .orElseThrow(EventQuestionNotFoundException::new);
        return question.getOptions().stream()
                .map(EventOptionResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventOptionResponseDto updateOption(Long optionId, EventOptionRequestDto request) {
        EventOption option = eventOptionRepository.findById(optionId)
                .orElseThrow(EventOptionNotFoundException::new);

        option.setText(request.getText());
        option.setIsCorrect(request.getIsCorrect());

        return EventOptionResponseDto.from(eventOptionRepository.save(option));
    }

    @Override
    @Transactional
    public void deleteOption(Long optionId) {
        EventOption option = eventOptionRepository.findById(optionId)
                .orElseThrow(EventOptionNotFoundException::new);
        eventOptionRepository.delete(option);
    }
}
