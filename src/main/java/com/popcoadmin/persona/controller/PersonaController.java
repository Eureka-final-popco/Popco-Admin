package com.popcoadmin.persona.controller;

import com.popcoadmin.common.response.ApiResponse;
import com.popcoadmin.persona.dto.request.PersonaRequestDto;
import com.popcoadmin.persona.dto.response.PersonaResponseDto;
import com.popcoadmin.persona.service.PersonaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/persona")
@Tag(name = "관리자용 페르소나 API", description = "페르소나 CRUD")
public class PersonaController {
    private final PersonaService personaService;

    @Operation(summary = "페르소나 신규 등록 API")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> insertPersona(@RequestBody PersonaRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponse.success(personaService.insertPersona(requestDto)));
    }

    @Operation(summary = "페르소나 단일 조회 API")
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<PersonaResponseDto>> getPersona(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(personaService.getPersona(id)));
    }

    @Operation(summary = "페르소나 리스트 조회 API")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<PersonaResponseDto>>> getPersonaList() {
        return ResponseEntity.ok(ApiResponse.success(personaService.getPersonaList()));
    }

    @Operation(summary = "페르소나 정보 수정 API")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> updatePersona(@RequestBody PersonaRequestDto requestDto, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(personaService.updatePersona(requestDto, id)));
    }

    @Operation(summary = "페르소나 삭제 API")
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deletePersona(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(personaService.deletePersona(id)));
    }
}
