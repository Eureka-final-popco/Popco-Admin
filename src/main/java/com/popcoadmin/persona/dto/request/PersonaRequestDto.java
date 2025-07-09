package com.popcoadmin.persona.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PersonaRequestDto {
    private String name;
    private String description;
    private String tag;
    private String imgPath;
}
