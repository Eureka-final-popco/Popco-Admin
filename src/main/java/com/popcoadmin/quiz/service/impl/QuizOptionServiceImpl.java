package com.popcoadmin.quiz.service.impl;

import com.popcoadmin.exception.BusinessException;
import com.popcoadmin.exception.ErrorCode;
import com.popcoadmin.quiz.dto.request.QuizOptionRequestDto;
import com.popcoadmin.quiz.dto.response.QuizOptionResponseDto;
import com.popcoadmin.quiz.entity.Quiz;
import com.popcoadmin.quiz.entity.QuizOption;
import com.popcoadmin.quiz.entity.QuizQuestion;
import com.popcoadmin.quiz.entity.key.QuizOptionId;
import com.popcoadmin.quiz.entity.key.QuizQuestionId;
import com.popcoadmin.quiz.repository.QuizOptionRepository;
import com.popcoadmin.quiz.repository.QuizQuestionRepository;
import com.popcoadmin.quiz.service.QuizOptionService;
import com.popcoadmin.exception.business.QuizQuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.popcoadmin.exception.business.QuizOptionNotFoundException;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizOptionServiceImpl implements QuizOptionService {

    private final QuizOptionRepository quizOptionRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    @Override
    @Transactional
    public QuizOptionResponseDto createQuizOption(Long quizId, Long questionId, QuizOptionRequestDto request) {
        if (request.getIsCorrect() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "정답 여부는 필수입니다.");
        }
        if (!StringUtils.hasText(request.getContent())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "선택지 내용은 필수입니다.");
        }

        QuizQuestionId quizQuestionId = QuizQuestionId.of(questionId, quizId);
        QuizQuestion quizQuestion = quizQuestionRepository.findById(quizQuestionId)
                .orElseThrow(() -> new QuizQuestionNotFoundException("Question ID: " + questionId + " 에 해당하는 질문을 찾을 수 없습니다."));

        if (!quizQuestion.getQuiz().getQuizId().equals(quizId)) {
            throw new QuizQuestionNotFoundException("Question ID: " + questionId + ", Quiz ID: " + quizId + " 에 해당하는 질문을 찾을 수 없습니다.");
        }

        Quiz quiz = quizQuestion.getQuiz();

        long currentCorrectOptions = quizQuestion.getOptions().stream().filter(QuizOption::getIsCorrect).count();
        if (request.getIsCorrect() && currentCorrectOptions >= 1) {
            throw new BusinessException(ErrorCode.INVALID_CORRECT_OPTION_COUNT, "질문에는 하나의 정답만 지정될 수 있습니다.");
        }

        QuizOption quizOption = QuizOption.of(request, quizQuestion, quiz);
        quizQuestion.addOption(quizOption);

        QuizOption savedOption = quizOptionRepository.save(quizOption);
        return QuizOptionResponseDto.from(savedOption);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizOptionResponseDto> getAllQuizOptions() {
        return quizOptionRepository.findAll().stream()
                .map(QuizOptionResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuizOptionResponseDto getQuizOptionById(Long optionId, Long questionId, Long quizId) {
        QuizOptionId quizOptionId = QuizOptionId.of(optionId, questionId, quizId);
        QuizOption quizOption = quizOptionRepository.findById(quizOptionId)
                .orElseThrow(() -> new QuizOptionNotFoundException("Option ID: " + optionId + " 에 해당하는 선택지를 찾을 수 없습니다."));

        if (!quizOption.getQuizQuestion().getQuestionId().equals(questionId) ||
                !quizOption.getQuizQuestion().getQuiz().getQuizId().equals(quizId)) {
            throw new QuizOptionNotFoundException("Option ID: " + optionId + ", Question ID: " + questionId + ", Quiz ID: " + quizId + " 에 해당하는 선택지를 찾을 수 없습니다.");
        }
        return QuizOptionResponseDto.from(quizOption);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizOptionResponseDto> getQuizOptionsByQuestionId(Long questionId, Long quizId) {
//        return quizOptionRepository.findByQuizQuestion_QuestionIdAndQuizQuestion_Quiz_QuizId(questionId, quizId).stream()
//                .map(QuizOptionResponseDto::from)
//                .collect(Collectors.toList());

        QuizQuestionId quizQuestionId = QuizQuestionId.of(questionId, quizId);

        // 1개의 질문을 찾음
        Optional<QuizQuestion> quizQuestion = quizQuestionRepository.findById(quizQuestionId);

        if (quizQuestion.isPresent()) {
            // 그 질문에 속한 여러개의 선택지들을 찾음
            List<QuizOption> options = quizOptionRepository.findByQuizQuestion(quizQuestion.get());

            return options.stream()
                    .map(QuizOptionResponseDto::from)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    @Transactional
    public QuizOptionResponseDto updateQuizOption(Long optionId, Long questionId, Long quizId, QuizOptionRequestDto request) {
        QuizOptionId quizOptionId = QuizOptionId.of(optionId, questionId, quizId);
        QuizOption option = quizOptionRepository.findById(quizOptionId)
                .orElseThrow(() -> new QuizOptionNotFoundException("Option ID: " + optionId + " 에 해당하는 선택지를 찾을 수 없습니다."));

        if (!option.getQuizQuestion().getQuestionId().equals(questionId) ||
                !option.getQuizQuestion().getQuiz().getQuizId().equals(quizId)) {
            throw new QuizOptionNotFoundException("Option ID: " + optionId + ", Question ID: " + questionId + ", Quiz ID: " + quizId + " 에 해당하는 선택지를 찾을 수 없습니다.");
        }

        if (request.getIsCorrect() != null && request.getIsCorrect()) {
            QuizQuestion quizQuestion = option.getQuizQuestion();
            long currentCorrectOptions = quizQuestion.getOptions().stream()
                    .filter(o -> !o.getOptionId().equals(optionId))
                    .filter(QuizOption::getIsCorrect)
                    .count();
            if (currentCorrectOptions >= 1) {
                throw new BusinessException(ErrorCode.INVALID_CORRECT_OPTION_COUNT, "질문에는 하나의 정답만 지정될 수 있습니다.");
            }
        } else if (request.getIsCorrect() != null && !request.getIsCorrect()) {
            QuizQuestion quizQuestion = option.getQuizQuestion();
            long correctOptionsExcludingThis = quizQuestion.getOptions().stream()
                    .filter(o -> !o.getOptionId().equals(optionId))
                    .filter(QuizOption::getIsCorrect)
                    .count();
            if (correctOptionsExcludingThis == 0 && quizQuestion.getOptions().size() > 1) {
                throw new BusinessException(ErrorCode.NO_CORRECT_OPTION, "이 옵션을 정답이 아니게 변경하면 질문에 정답이 없어집니다.");
            }
        }

        option.update(request);
        return QuizOptionResponseDto.from(quizOptionRepository.save(option));
    }

    @Override
    @Transactional
    public void deleteQuizOption(Long optionId, Long questionId, Long quizId) {
        QuizOptionId quizOptionId = QuizOptionId.of(optionId, questionId, quizId);
        QuizOption optionToDelete = quizOptionRepository.findById(quizOptionId)
                .orElseThrow(() -> new QuizOptionNotFoundException("Option ID: " + optionId + " 에 해당하는 선택지를 찾을 수 없습니다."));

        if (!optionToDelete.getQuizQuestion().getQuestionId().equals(questionId) ||
                !optionToDelete.getQuizQuestion().getQuiz().getQuizId().equals(quizId)) {
            throw new QuizOptionNotFoundException("Option ID: " + optionId + ", Question ID: " + questionId + ", Quiz ID: " + quizId + " 에 해당하는 선택지를 찾을 수 없습니다.");
        }

        QuizQuestion quizQuestion = optionToDelete.getQuizQuestion();
        if (optionToDelete.getIsCorrect()) {
            long remainingCorrectOptions = quizQuestion.getOptions().stream()
                    .filter(o -> !o.getOptionId().equals(optionId))
                    .filter(QuizOption::getIsCorrect)
                    .count();
            if (remainingCorrectOptions == 0 && quizQuestion.getOptions().size() > 1) {
                throw new BusinessException(ErrorCode.NO_CORRECT_OPTION, "이 옵션을 삭제하면 질문에 정답이 없어집니다. 다른 정답을 먼저 지정해주세요.");
            }
        }

        quizOptionRepository.deleteById(quizOptionId);
    }
}