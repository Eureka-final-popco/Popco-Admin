package com.popcoadmin.content.controller;

import com.popcoadmin.content.entity.BatchFailureLog;
import com.popcoadmin.content.schedule.DailyPopularContentSchedule;
import com.popcoadmin.content.service.BatchFailureLogService;
import com.popcoadmin.content.service.impl.BatchFailureLogServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contents/batch")
@Tag(name = "Content API", description = "콘텐츠 배치 관련 API")
public class ContentPopularBatchController {
    private final DailyPopularContentSchedule dailyPopularContentSchedule;
    private final BatchFailureLogService batchFailureLogService;

    @Operation(summary = "주간 인기 콘텐츠 수동 실행", description = "주간 인기 콘텐츠를 수동으로 실행")
    @PostMapping("/popular-content/run")
    public ResponseEntity<?> triggerBatch() {
        dailyPopularContentSchedule.runManually();
        return ResponseEntity.ok("배치 실행 완료");
    }

    @Operation(summary = "배치 실패 STEP 조회", description = "주간 인기 콘텐츠 배치 실패 리스트 STEP 조회")
    @GetMapping("/failures/unprocessed")
    public ResponseEntity<List<BatchFailureLog>> getUnprocessedFailures() {
        List<BatchFailureLog> failures = batchFailureLogService.getUnprocessedFailures();
        return ResponseEntity.ok(failures);
    }

    @Operation(summary = "배치 실패 STEP 재실행", description = "주간 인기 콘텐츠 배치 실패 STEP 재실행")
    @PostMapping("/failures/{failureLogId}/process")
    public ResponseEntity<String> markAsProcessed(@PathVariable Long failureLogId) {
        batchFailureLogService.markAsProcessed(failureLogId);
        return ResponseEntity.ok("처리 완료");
    }

    @Operation(summary = "배치 실패 JOB, STEP 이름으로 조회", description = "주간 인기 콘텐츠 배치 실패 리스트 JOB, STEP 이름으로 조회")
    @GetMapping("/failures/job/{jobName}/step/{stepName}")
    public ResponseEntity<List<BatchFailureLog>> getFailuresByJobAndStep(
            @PathVariable String jobName,
            @PathVariable String stepName) {

        List<BatchFailureLog> failures = batchFailureLogService.getFailuresByJobAndStep(jobName, stepName);
        return ResponseEntity.ok(failures);
    }
}
