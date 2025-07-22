package com.popcoadmin.content.controller;

import com.popcoadmin.common.response.ApiResponse;
import com.popcoadmin.content.schedule.DailyPopularContentSchedule;
import com.popcoadmin.content.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
@Tag(name = "Content API", description = "콘텐츠 관련 CRUD")
public class ContentController {
    private final ContentService contentService;
    private final DailyPopularContentSchedule dailyPopularContentSchedule;

    @Operation(summary = "영화 데이터 가져오기", description = "영화 데이터를 가져옵니다.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Void>> syncAll(int pagesPerCategory, boolean includeDetails) {
        // 비동기로 실행
        new Thread(() -> {
            contentService.syncAllContentData(pagesPerCategory, includeDetails);
        }).start();

        return ResponseEntity.ok(ApiResponse.success("영화, TV 시리즈 데이터 가져오기 성공", null));
    }

    @PostMapping("/batch/popular-content/run")
    public ResponseEntity<?> triggerBatch() {
        dailyPopularContentSchedule.runManually();
        return ResponseEntity.ok("배치 실행 완료");
    }

}
