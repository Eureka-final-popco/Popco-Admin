package com.popcoadmin.persona.service;

import com.popcoadmin.persona.dto.request.PersonaRequestDto;
import com.popcoadmin.persona.dto.response.PersonaResponseDto;

import java.util.List;

public interface PersonaService {
    Void insertPersona(PersonaRequestDto requestDto);
    PersonaResponseDto getPersona(Long id);
    List<PersonaResponseDto> getPersonaList();
    Void updatePersona(PersonaRequestDto requestDto, Long id);
    Void deletePersona(Long id);
}
