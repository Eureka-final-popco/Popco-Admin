package com.popcoadmin.content.repository;

import com.popcoadmin.content.entity.ContentGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentGenreRepository extends JpaRepository<ContentGenre, Long> {
}
