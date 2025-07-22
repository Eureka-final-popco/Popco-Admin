package com.popcoadmin.persona.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Table(name = "persona_options")
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personaOptionId;

    @ManyToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @ManyToOne
    @JoinColumns({
            @JoinColumn (name = "option_id", referencedColumnName = "option_id"),
            @JoinColumn (name = "question_id", referencedColumnName = "question_id")
    })
    private Option option;

    private BigDecimal score;
}
