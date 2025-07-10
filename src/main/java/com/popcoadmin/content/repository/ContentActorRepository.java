package com.popcoadmin.content.repository;

import com.popcoadmin.content.entity.ContentActor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentActorRepository extends JpaRepository<ContentActor, Long> {
}