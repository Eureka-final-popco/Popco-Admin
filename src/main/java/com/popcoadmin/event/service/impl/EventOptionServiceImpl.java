package com.popcoadmin.event.service.impl;

import com.popcoadmin.event.dto.request.EventOptionRequestDto;
import com.popcoadmin.event.dto.response.EventOptionResponseDto;
import com.popcoadmin.event.entity.EventOption;
import com.popcoadmin.event.entity.EventQuestion;
import com.popcoadmin.event.repository.EventOptionRepository;
import com.popcoadmin.event.repository.EventQuestionRepository;
import com.popcoadmin.event.service.EventOptionService;
import com.popcoadmin.exception.business.EventQuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.popcoadmin.exception.business.EventOptionNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventOptionServiceImpl implements EventOptionService {

    private final EventOptionRepository eventOptionRepository;
    private final EventQuestionRepository eventQuestionRepository;

    @Override
    @Transactional
    public EventOptionResponseDto createEventOption(Long questionId, EventOptionRequestDto requestDto) {
        EventQuestion question = eventQuestionRepository.findById(questionId)
                .orElseThrow(EventQuestionNotFoundException::new);

        EventOption option = EventOption.builder()
                .text(requestDto.getText())
                .isCorrect(requestDto.getIsCorrect())
                .question(question)
                .build();

        EventOption savedOption = eventOptionRepository.save(option);
        return EventOptionResponseDto.of(savedOption);
    }

    @Override
    @Transactional
    public EventOptionResponseDto getEventOption(Long optionId) {
        EventOption option = eventOptionRepository.findById(optionId)
                .orElseThrow(EventOptionNotFoundException::new);
        return EventOptionResponseDto.of(option);
    }

    @Override
    @Transactional
    public List<EventOptionResponseDto> getEventOptionsByQuestion(Long questionId) {
        if (!eventQuestionRepository.existsById(questionId)) {
            throw new EventQuestionNotFoundException("EventQuestion with ID " + questionId + " not found.");
        }

        return eventOptionRepository.findByQuestion_QuestionId(questionId).stream()
                .map(EventOptionResponseDto::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventOptionResponseDto updateEventOption(Long optionId, EventOptionRequestDto requestDto) {
        EventOption option = eventOptionRepository.findById(optionId)
                .orElseThrow(EventOptionNotFoundException::new);

        option.setText(requestDto.getText());
        option.setIsCorrect(requestDto.getIsCorrect());

        return EventOptionResponseDto.of(option);
    }

    @Override
    @Transactional
    public void deleteEventOption(Long optionId) {
        EventOption option = eventOptionRepository.findById(optionId)
                .orElseThrow(EventOptionNotFoundException::new);
        eventOptionRepository.delete(option);
    }
}
