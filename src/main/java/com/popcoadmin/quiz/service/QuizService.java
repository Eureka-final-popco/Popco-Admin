package com.popcoadmin.quiz.service;

import com.popcoadmin.quiz.dto.request.QuizRequestDto;
import com.popcoadmin.quiz.dto.response.QuizResponseDto;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface QuizService {
    QuizResponseDto createQuiz(QuizRequestDto request);
    List<QuizResponseDto> getAllQuizzes();
    QuizResponseDto getQuizById(Long id);
    QuizResponseDto updateQuiz(Long id, QuizRequestDto request);
    void deleteQuiz(Long id);
}
