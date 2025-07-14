package com.popcoadmin.quiz.service.impl;

import com.popcoadmin.exception.BusinessException;
import com.popcoadmin.exception.ErrorCode;
import com.popcoadmin.quiz.dto.request.QuizOptionRequestDto;
import com.popcoadmin.quiz.dto.request.QuizQuestionRequestDto;
import com.popcoadmin.quiz.dto.response.QuizQuestionResponseDto;
import com.popcoadmin.quiz.entity.Quiz;
import com.popcoadmin.quiz.entity.QuizOption;
import com.popcoadmin.quiz.entity.QuizQuestion;
import com.popcoadmin.quiz.repository.QuizQuestionRepository;
import com.popcoadmin.quiz.repository.QuizRepository;
import com.popcoadmin.quiz.service.QuizQuestionService;
import com.popcoadmin.exception.business.QuizNotFoundException;
import com.popcoadmin.exception.business.QuizQuestionNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizQuestionServiceImpl implements QuizQuestionService {

    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizRepository quizRepository;

    @Override
    @Transactional
    public QuizQuestionResponseDto createQuizQuestion(Long quizId, QuizQuestionRequestDto request) {
        if (request.getQuestionOrder() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "질문 순서는 필수입니다.");
        }
        if (!StringUtils.hasText(request.getContent())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "질문 내용은 필수입니다.");
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("ID: " + quizId + " 에 해당하는 퀴즈를 찾을 수 없습니다."));

        if (quizQuestionRepository.findByQuiz_QuizIdAndQuestionOrder(quizId, request.getQuestionOrder()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_QUESTION_ORDER, "해당 퀴즈에 이미 동일한 순서의 질문이 존재합니다: " + request.getQuestionOrder());
        }

        QuizQuestion quizQuestion = QuizQuestion.of(request);
        quizQuestion.setQuiz(quiz);

        quiz.addQuestion(quizQuestion);

        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            long correctOptionCount = request.getOptions().stream().filter(QuizOptionRequestDto::getIsCorrect).count();
            if (correctOptionCount == 0) {
                throw new BusinessException(ErrorCode.NO_CORRECT_OPTION, "질문에는 반드시 하나의 정답이 있어야 합니다.");
            }
            if (correctOptionCount > 1) {
                throw new BusinessException(ErrorCode.INVALID_CORRECT_OPTION_COUNT, "질문에는 하나의 정답만 지정될 수 있습니다.");
            }

            for (QuizOptionRequestDto optionDto : request.getOptions()) {
                QuizOption option = QuizOption.of(optionDto, quizQuestion, quiz);
                quizQuestion.addOption(option);
            }
        }

        QuizQuestion savedQuestion = quizQuestionRepository.save(quizQuestion);
        return QuizQuestionResponseDto.from(savedQuestion);
    }

    @Override
    @Transactional
    public List<QuizQuestionResponseDto> getAllQuizQuestions() {
        return quizQuestionRepository.findAll().stream()
                .map(QuizQuestionResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizQuestionResponseDto getQuizQuestionById(Long questionId, Long quizId) {
        QuizQuestion quizQuestion = quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new QuizQuestionNotFoundException("ID: " + questionId + " 에 해당하는 질문을 찾을 수 없습니다."));

        if (!quizQuestion.getQuiz().getQuizId().equals(quizId)) {
            throw new QuizQuestionNotFoundException("ID: " + questionId + ", Quiz ID: " + quizId + " 에 해당하는 질문을 찾을 수 없습니다.");
        }

        return QuizQuestionResponseDto.from(quizQuestion);
    }

    @Override
    @Transactional
    public List<QuizQuestionResponseDto> getQuizQuestionsByQuizId(Long quizId) {
        return quizQuestionRepository.findByQuiz_QuizIdOrderByQuestionOrderAsc(quizId).stream()
                .map(QuizQuestionResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizQuestionResponseDto updateQuizQuestion(Long questionId, Long quizId, QuizQuestionRequestDto request) {
        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new QuizQuestionNotFoundException("ID: " + questionId + " 에 해당하는 질문을 찾을 수 없습니다."));

        if (!question.getQuiz().getQuizId().equals(quizId)) {
            throw new QuizQuestionNotFoundException("ID: " + questionId + ", Quiz ID: " + quizId + " 에 해당하는 질문을 찾을 수 없습니다.");
        }

        Quiz quiz = question.getQuiz();

        if (request.getQuestionOrder() != null) {
            quizQuestionRepository.findByQuiz_QuizIdAndQuestionOrder(quizId, request.getQuestionOrder())
                    .ifPresent(existingQuestion -> {
                        if (!existingQuestion.getQuestionId().equals(questionId)) {
                            throw new BusinessException(ErrorCode.DUPLICATE_QUESTION_ORDER, "해당 퀴즈에 이미 동일한 순서의 질문이 존재합니다: " + request.getQuestionOrder());
                        }
                    });
        }

        question.update(request);

        if (request.getOptions() != null) {
            long correctOptionCount = request.getOptions().stream().filter(QuizOptionRequestDto::getIsCorrect).count();
            if (correctOptionCount == 0) {
                throw new BusinessException(ErrorCode.NO_CORRECT_OPTION, "질문에는 반드시 하나의 정답이 있어야 합니다.");
            }
            if (correctOptionCount > 1) {
                throw new BusinessException(ErrorCode.INVALID_CORRECT_OPTION_COUNT, "질문에는 하나의 정답만 지정될 수 있습니다.");
            }

            question.getOptions().clear();
            for (QuizOptionRequestDto optionDto : request.getOptions()) {
                QuizOption newOption = QuizOption.of(optionDto, question, quiz);
                question.addOption(newOption);
            }
        } else {
            question.getOptions().clear();
        }

        return QuizQuestionResponseDto.from(quizQuestionRepository.save(question));
    }

    @Override
    @Transactional
    public void deleteQuizQuestion(Long questionId, Long quizId) {
        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new QuizQuestionNotFoundException("ID: " + questionId + " 에 해당하는 질문을 찾을 수 없습니다."));

        if (!question.getQuiz().getQuizId().equals(quizId)) {
            throw new QuizQuestionNotFoundException("ID: " + questionId + ", Quiz ID: " + quizId + " 에 해당하는 질문을 찾을 수 없습니다.");
        }

        quizQuestionRepository.deleteById(questionId);
    }
}