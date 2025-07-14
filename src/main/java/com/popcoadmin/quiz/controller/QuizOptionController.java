package com.popcoadmin.quiz.controller;

import com.popcoadmin.common.response.ApiResponse;
import com.popcoadmin.quiz.dto.request.QuizOptionRequestDto;
import com.popcoadmin.quiz.dto.response.QuizOptionResponseDto;
import com.popcoadmin.quiz.service.QuizOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Quiz Option", description = "퀴즈 선택지 관련 API")
@RestController
@RequestMapping("/quiz-options")
@RequiredArgsConstructor
public class QuizOptionController {

    private final QuizOptionService quizOptionService;

    @Operation(summary = "새 퀴즈 선택지 생성", description = "특정 퀴즈 질문에 새로운 선택지를 생성합니다.")
    @PostMapping("/{quizId}/{questionId}")
    public ResponseEntity<ApiResponse<QuizOptionResponseDto>> createQuizOption(@PathVariable Long quizId, @PathVariable Long questionId, @RequestBody QuizOptionRequestDto request) {
        QuizOptionResponseDto createdOption = quizOptionService.createQuizOption(quizId, questionId, request);
        return ResponseEntity.ok(ApiResponse.success("선택지 생성 성공", createdOption));
    }

    @Operation(summary = "모든 퀴즈 선택지 조회", description = "등록된 모든 퀴즈 선택지 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<QuizOptionResponseDto>>> getAllQuizOptions() {
        List<QuizOptionResponseDto> options = quizOptionService.getAllQuizOptions();
        return ResponseEntity.ok(ApiResponse.success("모든 선택지 조회 성공", options));
    }

    @Operation(summary = "특정 퀴즈 선택지 조회", description = "퀴즈 ID, 질문 ID, 선택지 ID를 통해 특정 선택지 정보를 조회합니다.")
    @GetMapping("/{quizId}/{questionId}/{optionId}")
    public ResponseEntity<ApiResponse<QuizOptionResponseDto>> getQuizOptionById(@PathVariable Long quizId, @PathVariable Long questionId, @PathVariable Long optionId) {
        QuizOptionResponseDto option = quizOptionService.getQuizOptionById(optionId, questionId, quizId);
        return ResponseEntity.ok(ApiResponse.success("특정 선택지 조회 성공", option));
    }

    @Operation(summary = "질문별 선택지 조회", description = "특정 퀴즈 질문 ID에 속한 모든 선택지 목록을 조회합니다.")
    @GetMapping("/by-question/{quizId}/{questionId}")
    public ResponseEntity<ApiResponse<List<QuizOptionResponseDto>>> getQuizOptionsByQuestionId(@PathVariable Long quizId, @PathVariable Long questionId) {
        List<QuizOptionResponseDto> options = quizOptionService.getQuizOptionsByQuestionId(questionId, quizId);
        return ResponseEntity.ok(ApiResponse.success("질문별 선택지 조회 성공", options));
    }

    @Operation(summary = "퀴즈 선택지 정보 수정", description = "퀴즈 ID, 질문 ID, 선택지 ID를 통해 특정 선택지 정보를 수정합니다.")
    @PutMapping("/{quizId}/{questionId}/{optionId}")
    public ResponseEntity<ApiResponse<QuizOptionResponseDto>> updateQuizOption(@PathVariable Long quizId, @PathVariable Long questionId, @PathVariable Long optionId, @RequestBody QuizOptionRequestDto request) {
        QuizOptionResponseDto updated = quizOptionService.updateQuizOption(optionId, questionId, quizId, request);
        return ResponseEntity.ok(ApiResponse.success("선택지 수정 성공", updated));
    }

    @Operation(summary = "퀴즈 선택지 삭제", description = "퀴즈 ID, 질문 ID, 선택지 ID를 통해 특정 선택지를 삭제합니다.")
    @DeleteMapping("/{quizId}/{questionId}/{optionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuizOption(@PathVariable Long quizId, @PathVariable Long questionId, @PathVariable Long optionId) {
        quizOptionService.deleteQuizOption(optionId, questionId, quizId);
        return ResponseEntity.ok(ApiResponse.success("선택지 삭제 성공", null));
    }
}