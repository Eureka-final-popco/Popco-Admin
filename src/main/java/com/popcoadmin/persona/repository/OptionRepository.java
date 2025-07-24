package com.popcoadmin.persona.repository;

import com.popcoadmin.persona.entity.Option;
import com.popcoadmin.persona.entity.key.OptionsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository <Option, OptionsId> {
}
