package com.popcoadmin.quiz.service;

import com.popcoadmin.quiz.dto.request.QuizOptionRequestDto;
import com.popcoadmin.quiz.dto.response.QuizOptionResponseDto;

import java.util.List;
import java.util.Optional;

public interface QuizOptionService {
    QuizOptionResponseDto createQuizOption(Long quizId, Long questionId, QuizOptionRequestDto request);
    List<QuizOptionResponseDto> getAllQuizOptions();
    QuizOptionResponseDto getQuizOptionById(Long optionId, Long questionId, Long quizId);
    List<QuizOptionResponseDto> getQuizOptionsByQuestionId(Long questionId, Long quizId);
    QuizOptionResponseDto updateQuizOption(Long optionId, Long questionId, Long quizId, QuizOptionRequestDto request);
    void deleteQuizOption(Long optionId, Long questionId, Long quizId);
}
