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
                .orElseThrow(() -> new EventQuestionNotFoundException("이벤트 질문을 찾을 수 없습니다. ID: " + questionId));

        EventOption option = EventOption.builder()
                .text(requestDto.getText())
                .isCorrect(requestDto.getIsCorrect())
                .question(question)
                .build();

        EventOption savedOption = eventOptionRepository.save(option);
        return EventOptionResponseDto.from(savedOption);
    }

    @Override
    @Transactional
    public EventOptionResponseDto getEventOption(Long optionId) {
        EventOption option = eventOptionRepository.findById(optionId)
                .orElseThrow(() -> new EventOptionNotFoundException("이벤트 옵션을 찾을 수 없습니다. ID: " + optionId));
        return EventOptionResponseDto.from(option);
    }

    @Override
    @Transactional
    public List<EventOptionResponseDto> getEventOptionsByQuestion(Long questionId) {
        EventQuestion question = eventQuestionRepository.findById(questionId) // existsById 대신 findById 사용
                .orElseThrow(() -> new EventQuestionNotFoundException("이벤트 질문을 찾을 수 없습니다. ID: " + questionId)); // 직접 한글 메시지 작성 + ID

        return eventOptionRepository.findByQuestion_QuestionId(questionId).stream()
                .map(EventOptionResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventOptionResponseDto updateEventOption(Long optionId, EventOptionRequestDto requestDto) {
        EventOption option = eventOptionRepository.findById(optionId)
                .orElseThrow(() -> new EventOptionNotFoundException("이벤트 옵션을 찾을 수 없습니다. ID: " + optionId));

        option.setText(requestDto.getText());
        option.setIsCorrect(requestDto.getIsCorrect());

        return EventOptionResponseDto.from(option);
    }

    @Override
    @Transactional
    public void deleteEventOption(Long optionId) {
        EventOption option = eventOptionRepository.findById(optionId)
                .orElseThrow(() -> new EventOptionNotFoundException("이벤트 옵션을 찾을 수 없습니다. ID: " + optionId));
        eventOptionRepository.delete(option);
    }
}
