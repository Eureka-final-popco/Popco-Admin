package com.popcoadmin.quiz.service;

import com.popcoadmin.quiz.dto.request.QuizQuestionRequestDto;
import com.popcoadmin.quiz.dto.response.QuizQuestionResponseDto;

import java.util.List;
import java.util.Optional;

public interface QuizQuestionService {
    QuizQuestionResponseDto createQuizQuestion(Long quizId, QuizQuestionRequestDto request);
    List<QuizQuestionResponseDto> getAllQuizQuestions();
    QuizQuestionResponseDto getQuizQuestionById(Long questionId, Long quizId);
    List<QuizQuestionResponseDto> getQuizQuestionsByQuizId(Long quizId);
    QuizQuestionResponseDto updateQuizQuestion(Long questionId, Long quizId, QuizQuestionRequestDto request);
    void deleteQuizQuestion(Long questionId, Long quizId);
}
