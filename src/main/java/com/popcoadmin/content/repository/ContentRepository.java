package com.popcoadmin.content.repository;

import com.popcoadmin.content.entity.Content;
import com.popcoadmin.content.entity.key.ContentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, ContentId> {
    @Query("SELECT c.id.id FROM Content c WHERE c.id.type = 'movie'")
    List<Long> findAllMovieIds();

    @Query("SELECT c.id.id FROM Content c WHERE c.id.type = 'tv'")
    List<Long> findAllTvIds();
}
