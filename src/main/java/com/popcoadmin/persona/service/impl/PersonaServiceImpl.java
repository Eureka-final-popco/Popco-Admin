package com.popcoadmin.persona.service.impl;

import com.popcoadmin.exception.BaseException;
import com.popcoadmin.exception.ErrorCode;
import com.popcoadmin.persona.dto.request.PersonaRequestDto;
import com.popcoadmin.persona.dto.response.PersonaResponseDto;
import com.popcoadmin.persona.entity.Persona;
import com.popcoadmin.persona.repository.PersonaRepository;
import com.popcoadmin.persona.service.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;

    @Override
    public Void createPersona(PersonaRequestDto requestDto) {
        Persona persona = Persona.from(requestDto);
        personaRepository.save(persona);
        return null;
    }

    @Override
    public PersonaResponseDto getPersona(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.PERSONA_NOT_FOUND, "페르소나 id: " + id));
        return PersonaResponseDto.from(persona);
    }

    @Override
    public List<PersonaResponseDto> getPersonaList() {
        List<Persona> personas = personaRepository.findAll();
        return personas.stream()
                .map(PersonaResponseDto::from)
                .toList();
    }

    @Override
    public Void updatePersona(PersonaRequestDto requestDto, Long id) {
        if (!personaRepository.existsById(id)) {
            throw new BaseException(ErrorCode.PERSONA_NOT_FOUND, "페르소나 id: " + id);
        }

        Persona persona = Persona.from(requestDto, id);
        personaRepository.save(persona);
        return null;
    }

    @Override
    public Void deletePersona(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.PERSONA_NOT_FOUND, "페르소나 id: " + id));

        personaRepository.delete(persona);
        return null;
    }
}
