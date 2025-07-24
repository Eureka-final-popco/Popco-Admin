package com.popcoadmin.persona.repository;

import com.popcoadmin.persona.entity.PersonaOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaOptionRepository extends JpaRepository<PersonaOption, Long> {
}
