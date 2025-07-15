package com.popcoadmin.quiz.controller;

import com.popcoadmin.common.response.ApiResponse;
import com.popcoadmin.quiz.dto.request.QuizQuestionRequestDto;
import com.popcoadmin.quiz.dto.response.QuizQuestionResponseDto;
import com.popcoadmin.quiz.service.QuizQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Quiz Question", description = "퀴즈 질문 관련 API")
@RestController
@RequestMapping("/quizzes-questions")
@RequiredArgsConstructor
public class QuizQuestionController {

    private final QuizQuestionService quizQuestionService;

    @Operation(summary = "새 퀴즈 질문 생성", description = "특정 퀴즈에 새로운 질문을 생성합니다.")
    @PostMapping("/quizzes/{quizId}")
    public ResponseEntity<ApiResponse<QuizQuestionResponseDto>> createQuizQuestion(@PathVariable Long quizId, @RequestBody QuizQuestionRequestDto request) {
        QuizQuestionResponseDto createdQuestion = quizQuestionService.createQuizQuestion(quizId, request);
        return ResponseEntity.ok(ApiResponse.success("질문 생성 성공", createdQuestion));
    }

    @Operation(summary = "모든 퀴즈 질문 조회", description = "등록된 모든 퀴즈 질문 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<QuizQuestionResponseDto>>> getAllQuizQuestions() {
        List<QuizQuestionResponseDto> questions = quizQuestionService.getAllQuizQuestions();
        return ResponseEntity.ok(ApiResponse.success("모든 퀴즈 질문 조회 성공", questions));
    }

    @Operation(summary = "특정 퀴즈 질문 조회", description = "퀴즈 ID와 질문 ID를 통해 특정 질문 정보를 조회합니다.")
    @GetMapping("/quizzes/{quizId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<QuizQuestionResponseDto>> getQuizQuestionById(@PathVariable Long quizId, @PathVariable Long questionId) {
        QuizQuestionResponseDto question = quizQuestionService.getQuizQuestionById(questionId, quizId);
        return ResponseEntity.ok(ApiResponse.success("특정 퀴즈 질문 조회 성공", question));
    }

    @Operation(summary = "퀴즈별 질문 조회", description = "특정 퀴즈 ID에 속한 모든 질문 목록을 조회합니다.")
    @GetMapping("/quizzes/{quizId}")
    public ResponseEntity<ApiResponse<List<QuizQuestionResponseDto>>> getQuizQuestionsByQuizId(@PathVariable Long quizId) {
        List<QuizQuestionResponseDto> questions = quizQuestionService.getQuizQuestionsByQuizId(quizId);
        return ResponseEntity.ok(ApiResponse.success("퀴즈별 질문 조회 성공", questions));
    }

    @Operation(summary = "퀴즈 질문 정보 수정", description = "퀴즈 ID와 질문 ID를 통해 특정 질문 정보를 수정합니다.")
    @PutMapping("/quizzes/{quizId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<QuizQuestionResponseDto>> updateQuizQuestion(@PathVariable Long quizId, @PathVariable Long questionId, @RequestBody QuizQuestionRequestDto request) {
        QuizQuestionResponseDto updated = quizQuestionService.updateQuizQuestion(questionId, quizId, request);
        return ResponseEntity.ok(ApiResponse.success("질문 수정 성공", updated));
    }

    @Operation(summary = "퀴즈 질문 삭제", description = "퀴즈 ID와 질문 ID를 통해 특정 질문을 삭제합니다.")
    @DeleteMapping("/quizzes/{quizId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuizQuestion(@PathVariable Long quizId, @PathVariable Long questionId) {
        quizQuestionService.deleteQuizQuestion(questionId, quizId);
        return ResponseEntity.ok(ApiResponse.success("질문 삭제 성공", null));
    }
}