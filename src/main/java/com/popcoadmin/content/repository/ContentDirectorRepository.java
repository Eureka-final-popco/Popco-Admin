package com.popcoadmin.content.repository;

import com.popcoadmin.content.entity.ContentDirector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentDirectorRepository extends JpaRepository<ContentDirector, Long> {
}
