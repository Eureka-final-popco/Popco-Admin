package com.popcoadmin.persona.entity.composite;

import lombok.Data;

import java.io.Serializable;

@Data
public class PersonaGenreId implements Serializable {
    private Long persona;
    private Long genre;
}
