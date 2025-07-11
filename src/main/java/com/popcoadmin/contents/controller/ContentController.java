package com.popcoadmin.contents.controller;

import com.popcoadmin.contents.dto.response.PopularListResponseDto;
import com.popcoadmin.contents.service.ContentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "콘텐츠 API", description = "TMDB API 이용한 자체 DB 관리")
@RequestMapping("/content")
public class ContentController {
    private final ContentService contentService;

    @GetMapping("/popularList")
    public ResponseEntity<PopularListResponseDto> popularList() {

    }
}
