package com.popcoadmin.persona.dto.response;

import com.popcoadmin.persona.entity.Persona;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PersonaResponseDto {
    private String name;
    private String description;
    private String tag;
    private String imgPath;

    public static PersonaResponseDto from(Persona persona){
        return PersonaResponseDto.builder()
                .name(persona.getName())
                .description(persona.getDescription())
                .tag(persona.getTag())
                .imgPath(persona.getImgPath())
                .build();
    }
}
