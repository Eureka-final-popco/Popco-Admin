package com.popcoadmin.persona.entity;

import com.popcoadmin.contents.entity.Genre;
import com.popcoadmin.persona.entity.composite.PersonaGenreId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PersonaGenreId.class)
public class PersonaGenre {
    @Id
    @ManyToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @Id
    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;
}