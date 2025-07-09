package com.popcoadmin.contents.repository;

import com.popcoadmin.contents.entity.ContentActor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentActorRepository extends JpaRepository<ContentActor, Long> {
}
