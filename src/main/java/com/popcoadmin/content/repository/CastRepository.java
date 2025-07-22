package com.popcoadmin.content.repository;

import com.popcoadmin.content.entity.CastMember;
import com.popcoadmin.content.entity.key.ContentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CastRepository extends JpaRepository<CastMember, Long> {
    void deleteByContent_id(ContentId contentId);
}