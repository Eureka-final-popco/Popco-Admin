package com.popcoadmin.content.repository;

import com.popcoadmin.content.entity.Content;
import com.popcoadmin.content.entity.ContentGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentGenreRepository extends JpaRepository<ContentGenre, Long> {

    List<ContentGenre> findByContent(Content content);
}
