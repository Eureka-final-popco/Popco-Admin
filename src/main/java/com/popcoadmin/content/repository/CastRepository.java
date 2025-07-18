package com.popcoadmin.content.repository;

import com.popcoadmin.content.entity.Cast;
import com.popcoadmin.content.entity.key.ContentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CastRepository extends JpaRepository<Cast, Long> {
    void deleteByContent_id(ContentId contentId);
}