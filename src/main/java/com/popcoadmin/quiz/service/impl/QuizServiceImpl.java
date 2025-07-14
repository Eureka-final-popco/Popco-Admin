package com.popcoadmin.quiz.service.impl;

import com.popcoadmin.exception.BusinessException;
import com.popcoadmin.exception.ErrorCode;
import com.popcoadmin.exception.business.QuizNotFoundException;
import com.popcoadmin.quiz.dto.request.QuizOptionRequestDto;
import com.popcoadmin.quiz.dto.request.QuizQuestionRequestDto;
import com.popcoadmin.quiz.dto.request.QuizRequestDto;
import com.popcoadmin.quiz.dto.response.QuizResponseDto;
import com.popcoadmin.quiz.entity.Quiz;
import com.popcoadmin.quiz.entity.QuizOption;
import com.popcoadmin.quiz.entity.QuizQuestion;
import com.popcoadmin.quiz.repository.QuizRepository;
import com.popcoadmin.quiz.service.QuizService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    @Override
    @Transactional
    public QuizResponseDto createQuiz(QuizRequestDto request) {
        if (!StringUtils.hasText(request.getName())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "퀴즈 이름은 필수입니다.");
        }
        if (request.getStartAt() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "시작 시간은 필수입니다.");
        }
        if (request.getEndAt() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "종료 시간은 필수입니다.");
        }
        if (request.getRoundCount() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "라운드 수는 필수입니다.");
        }

        if (quizRepository.findByName(request.getName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_QUIZ_NAME, "이미 존재하는 퀴즈 이름입니다: " + request.getName());
        }

        Quiz quiz = Quiz.of(request);

        if(request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            for(QuizQuestionRequestDto questionDto : request.getQuestions()) {
                QuizQuestion question = QuizQuestion.of(questionDto);
                quiz.addQuestion(question);

                if(questionDto.getOptions() != null) {
                    long correctOptionCount = questionDto.getOptions().stream().filter(QuizOptionRequestDto::getIsCorrect).count();
                    if (correctOptionCount == 0) {
                        throw new BusinessException(ErrorCode.NO_CORRECT_OPTION, "질문에는 반드시 하나의 정답이 있어야 합니다.");
                    }
                    if (correctOptionCount > 1) {
                        throw new BusinessException(ErrorCode.INVALID_CORRECT_OPTION_COUNT, "질문에는 하나의 정답만 지정될 수 있습니다.");
                    }

                    questionDto.getOptions().forEach(optionDto -> {
                        QuizOption option = QuizOption.of(optionDto, question, quiz);
                        question.addOption(option);
                    });
                }
            }
        }

        Quiz savedQuiz = quizRepository.save(quiz);
        return QuizResponseDto.from(savedQuiz);
    }

    @Override
    @Transactional
    public List<QuizResponseDto> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(QuizResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizResponseDto getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("ID: " + id + " 에 해당하는 퀴즈를 찾을 수 없습니다."));
        return QuizResponseDto.from(quiz);
    }

    @Override
    @Transactional
    public QuizResponseDto updateQuiz(Long id, QuizRequestDto request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("ID: " + id + " 에 해당하는 퀴즈를 찾을 수 없습니다."));

        if(StringUtils.hasText(request.getName())) {
            quizRepository.findByName(request.getName()).ifPresent(existingQuiz -> {
                if(!existingQuiz.getQuizId().equals(id)) {
                    throw new BusinessException(ErrorCode.DUPLICATE_QUIZ_NAME, "이미 존재하는 퀴즈 이름입니다: " + request.getName());
                }
            });
        }
        quiz.update(request);

        if (request.getQuestions() != null) {
            quiz.getQuestions().clear();
            for (QuizQuestionRequestDto questionDto : request.getQuestions()) {
                QuizQuestion newQuestion = QuizQuestion.of(questionDto);
                newQuestion.setQuiz(quiz);
                quiz.addQuestion(newQuestion);

                if (questionDto.getOptions() != null) {
                    long correctOptionCount = questionDto.getOptions().stream().filter(QuizOptionRequestDto::getIsCorrect).count();
                    if (correctOptionCount == 0) {
                        throw new BusinessException(ErrorCode.NO_CORRECT_OPTION, "질문에는 반드시 하나의 정답이 있어야 합니다.");
                    }
                    if (correctOptionCount > 1) {
                        throw new BusinessException(ErrorCode.INVALID_CORRECT_OPTION_COUNT, "질문에는 하나의 정답만 지정될 수 있습니다.");
                    }
                    for (QuizOptionRequestDto optionDto : questionDto.getOptions()) {
                        QuizOption newOption = QuizOption.of(optionDto, newQuestion, quiz);
                        newQuestion.addOption(newOption);
                    }
                }
            }
        } else {
            quiz.getQuestions().clear();
        }

        return QuizResponseDto.from(quizRepository.save(quiz));
    }

    @Override
    public void deleteQuiz(Long id) {
        quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("ID: " + id + " 에 해당하는 퀴즈를 찾을 수 없습니다."));
        quizRepository.deleteById(id);
    }
}
