package com.popcoadmin.quiz.controller;

import com.popcoadmin.common.response.ApiResponse;
import com.popcoadmin.quiz.dto.request.QuizRequestDto;
import com.popcoadmin.quiz.dto.request.TestNotificationRequestDto;
import com.popcoadmin.quiz.dto.response.QuizResponseDto;
import com.popcoadmin.quiz.service.QuizService;
import com.popcoadmin.quiz.service.impl.NotificationServiceImpl;
import com.popcoadmin.quiz.service.impl.QuizNotificationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Quiz", description = "선착순 퀴즈 관련 API")
@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final QuizNotificationServiceImpl quizNotificationService;

    @Operation(summary = "새 퀴즈 생성", description = "새로운 퀴즈를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<QuizResponseDto>> createQuiz(@RequestBody QuizRequestDto request) {
        QuizResponseDto createdQuiz = quizService.createQuiz(request);
        return ResponseEntity.ok(ApiResponse.success("퀴즈 생성 성공", createdQuiz));
    }

    @Operation(summary = "모든 퀴즈 조회", description = "등록된 모든 퀴즈 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<QuizResponseDto>>> getAllQuizzes() {
        List<QuizResponseDto> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(ApiResponse.success("모든 퀴즈 조회 성공", quizzes));
    }

    @Operation(summary = "특정 퀴즈 조회", description = "ID를 통해 특정 퀴즈 정보를 조회합니다.")
    @GetMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> getQuizById(@PathVariable Long quizId) {
        QuizResponseDto event = quizService.getQuizById(quizId);
        return ResponseEntity.ok(ApiResponse.success("특정 퀴즈 조회 성공", event));
    }

    @Operation(summary = "퀴즈 정보 수정", description = "ID를 통해 특정 퀴즈 정보를 수정합니다.")
    @PutMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> updateQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizRequestDto request) {
        QuizResponseDto updatedQuiz = quizService.updateQuiz(quizId, request);
        return ResponseEntity.ok(ApiResponse.success("퀴즈 수정 성공", updatedQuiz));
    }

    @Operation(summary = "퀴즈 삭제", description = "ID를 통해 특정 퀴즈를 삭제합니다.")
    @DeleteMapping("/{quizId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.ok(ApiResponse.success("퀴즈 삭제 성공", null));
    }

    @Operation(summary = "알림 발송 테스트 api", description = "#개발 및 디버깅 용도의 api 입니다.")
    @PostMapping("/test-notification")
    public ResponseEntity<ApiResponse<String>> sendTestNotification(@RequestBody TestNotificationRequestDto request) {
        quizNotificationService.sendTestNotification(request.getTitle(), request.getMessage());
        return ResponseEntity.ok(ApiResponse.success("테스트 알림 발송 성공",null));
    }

    @Operation(summary = "특정 퀴즈 채널 알림 발송 API", description = "페이지 로드 시, GET /api/client/quizzes/latest 로 발생한 가장 최근 퀴즈 id 를 받아와 넘겨주면 임박한 퀴즈 알림 채널 구독 가능")
    @PostMapping("/{quizId}/send-notification")
    public ResponseEntity<ApiResponse<Void>> sendQuizNotification(@PathVariable Long quizId) {
        quizNotificationService.sendQuizNotification(quizId);
        return ResponseEntity.ok(ApiResponse.success("퀴즈 알림 발송 성공", null));
    }
}
