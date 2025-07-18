package com.popcoadmin.content.repository;

import com.popcoadmin.content.entity.Crew;
import com.popcoadmin.content.entity.key.ContentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewRepository extends JpaRepository<Crew, Long> {
    void deleteByContent_Id(ContentId contentId);
}
